package ru.vladimir.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vladimir.tgBot.entity.*;

import java.util.List;
import java.util.Optional;

@Service
public class TelegramBotService {
    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    private final EntitiesService entitiesService;
    private TelegramBot bot;

    @Value("${bot.token}")
    private String token;

    public TelegramBotService(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    @PostConstruct
    public void init() {
        try {
            bot = new TelegramBot(token);
            bot.setUpdatesListener(updates -> {
                updates.forEach(this::processUpdate);
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }, e -> {
                if (e != null) {
                    log.error("Error in updates listener", e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to initialize Telegram bot", e);
            throw new RuntimeException("Bot initialization failed", e);
        }
    }

    private void processUpdate(Update update) {
        try {
            if (update == null) {
                log.warn("Received null update");
                return;
            }

            if (update.callbackQuery() != null) {
                processCallbackQuery(update);
            } else if (update.message() != null && update.message().text() != null) {
                processMessage(update);
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
            try {
                if (update != null && update.message() != null && update.message().chat() != null) {
                    bot.execute(new SendMessage(update.message().chat().id(),
                            "Произошла ошибка. Пожалуйста, попробуйте позже."));
                }
            } catch (Exception ex) {
                log.error("Failed to send error message", ex);
            }
        }
    }

    private void processCallbackQuery(Update update) {
        Long chatId = null;
        try {
            chatId = update.callbackQuery().message().chat().id();
            String callbackData = update.callbackQuery().data();

            if (callbackData.startsWith("product:")) {
                Long productId = Long.parseLong(callbackData.split(":")[1]);
                addProductToOrder(chatId, productId);
            } else if (callbackData.startsWith("category:")) {
                Long categoryId = Long.parseLong(callbackData.split(":")[1]);
                showCategoryMenu(chatId, categoryId);
            }
        } catch (Exception e) {
            log.error("Error processing callback query for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка обработки вашего запроса");
        }
    }

    private void processMessage(Update update) {
        Long chatId = null;
        try {
            chatId = update.message().chat().id();
            String text = update.message().text();

            if ("/start".equals(text)) {
                handleStartCommand(chatId, update);
            } else if (text.matches(".*\\d.*")) {
                updateClientPhone(chatId, text);
            } else if ("Оформить заказ".equals(text)) {
                handleCheckoutCommand(chatId);
            } else if ("В основное меню".equals(text)) {
                showMainMenu(chatId);
            } else {
                handleCategorySelection(chatId, text);
            }
        } catch (Exception e) {
            log.error("Error processing message for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка обработки сообщения");
        }
    }

    private void handleStartCommand(Long chatId, Update update) {
        try {
            Client client = getOrCreateClient(chatId, update);
            ensureActiveOrder(client);
            showMainMenu(chatId);
        } catch (Exception e) {
            log.error("Error in start command for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка при запуске бота");
        }
    }

    private Client getOrCreateClient(Long chatId, Update update) {
        try {
            Optional<Client> existingClient = entitiesService.findClientByExternalId(chatId);

            if (existingClient.isPresent()) {
                return existingClient.get();
            }

            Client newClient = new Client();
            newClient.setExternalId(chatId);

            String firstName = update.message().chat().firstName() != null ?
                    update.message().chat().firstName() : "";
            String lastName = update.message().chat().lastName() != null ?
                    update.message().chat().lastName() : "";
            newClient.setFullName((firstName + " " + lastName).trim());

            newClient.setPhoneNumber("Не указан");
            newClient.setAddress("Не указан");

            return entitiesService.saveClient(newClient);
        } catch (Exception e) {
            log.error("Error creating client for chatId: " + chatId, e);
            throw new RuntimeException("Failed to create client", e);
        }
    }

    private void ensureActiveOrder(Client client) {
        try {
            List<ClientOrder> activeOrders = entitiesService.getClientOrdersByStatus(client.getId(), 1);
            if (activeOrders.isEmpty()) {
                ClientOrder newOrder = new ClientOrder();
                newOrder.setClient(client);
                newOrder.setStatus(1);
                newOrder.setTotal(0.0);
                entitiesService.saveOrder(newOrder);
            }
        } catch (Exception e) {
            log.error("Error ensuring active order for client: " + client.getId(), e);
            throw new RuntimeException("Failed to ensure active order", e);
        }
    }

    private void showMainMenu(Long chatId) {
        try {
            List<Category> mainCategories = entitiesService.getCategoriesByParentId(null);

            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(
                    mainCategories.stream()
                            .map(c -> new KeyboardButton(c.getName()))
                            .toArray(KeyboardButton[]::new)
            );
            markup.resizeKeyboard(true);
            markup.addRow(new KeyboardButton("Оформить заказ"));

            bot.execute(new SendMessage(chatId, "Выберите категорию:").replyMarkup(markup));
        } catch (Exception e) {
            log.error("Error showing main menu for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка при загрузке меню");
        }
    }

    private void handleCategorySelection(Long chatId, String categoryName) {
        try {
            Optional<Category> category = entitiesService.findCategoryByName(categoryName);
            if (category.isPresent()) {
                showCategoryMenu(chatId, category.get().getId());
            } else {
                bot.execute(new SendMessage(chatId, "Категория не найдена"));
            }
        } catch (Exception e) {
            log.error("Error handling category selection for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка при выборе категории");
        }
    }

    private void showCategoryMenu(Long chatId, Long categoryId) {
        try {
            Category category = entitiesService.getCategoryById(categoryId);
            List<Category> subCategories = entitiesService.getCategoriesByParentId(categoryId);
            List<Product> products = entitiesService.getProductsByCategoryId(categoryId);

            if (!subCategories.isEmpty()) {
                ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(
                        subCategories.stream()
                                .map(c -> new KeyboardButton(c.getName()))
                                .toArray(KeyboardButton[]::new)
                );
                markup.resizeKeyboard(true);
                markup.addRow(new KeyboardButton("Оформить заказ"));
                markup.addRow(new KeyboardButton("В основное меню"));

                bot.execute(new SendMessage(chatId, "Выберите подкатегорию:").replyMarkup(markup));
            } else if (!products.isEmpty()) {
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                products.forEach(product -> {
                    markup.addRow(new InlineKeyboardButton(
                            String.format("%s - %.2f руб.", product.getName(), product.getPrice())
                    ).callbackData("product:" + product.getId()));
                });

                ReplyKeyboardMarkup navigationMarkup = new ReplyKeyboardMarkup(
                        new KeyboardButton("Оформить заказ"),
                        new KeyboardButton("В основное меню")
                );
                navigationMarkup.resizeKeyboard(true);

                bot.execute(new SendMessage(chatId, "Товары категории " + category.getName() + ":")
                        .replyMarkup(markup));
                bot.execute(new SendMessage(chatId, "Выберите товар:")
                        .replyMarkup(navigationMarkup));
            } else {
                bot.execute(new SendMessage(chatId, "В этой категории нет товаров"));
            }
        } catch (Exception e) {
            log.error("Error showing category menu for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка при загрузке категории");
        }
    }

    private void addProductToOrder(Long chatId, Long productId) {
        try {
            Optional<Client> client = entitiesService.findClientByExternalId(chatId);
            if (client.isEmpty()) {
                sendErrorMessage(chatId, "Клиент не найден");
                return;
            }

            List<ClientOrder> activeOrders = entitiesService.getClientOrdersByStatus(client.get().getId(), 1);
            if (activeOrders.isEmpty()) {
                sendErrorMessage(chatId, "Нет активного заказа");
                return;
            }

            Product product = entitiesService.getProductById(productId);
            if (product == null) {
                sendErrorMessage(chatId, "Товар не найден");
                return;
            }

            entitiesService.addProductToOrder(activeOrders.get(0).getId(), productId, 1);
            bot.execute(new SendMessage(chatId, "Товар " + product.getName() + " добавлен в заказ"));
        } catch (Exception e) {
            log.error("Error adding product to order for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка при добавлении товара");
        }
    }

    private void handleCheckoutCommand(Long chatId) {
        try {
            Optional<Client> client = entitiesService.findClientByExternalId(chatId);
            if (client.isEmpty()) {
                sendErrorMessage(chatId, "Клиент не найден");
                return;
            }

            List<ClientOrder> activeOrders = entitiesService.getClientOrdersByStatus(client.get().getId(), 1);
            if (activeOrders.isEmpty()) {
                sendErrorMessage(chatId, "Нет активного заказа");
                return;
            }

            ClientOrder order = activeOrders.get(0);
            List<OrderProduct> orderProducts = entitiesService.getOrderProducts(order.getId());

            if (orderProducts.isEmpty()) {
                bot.execute(new SendMessage(chatId, "Ваш заказ пуст"));
                return;
            }

            order.setStatus(2);
            entitiesService.saveOrder(order);

            StringBuilder message = new StringBuilder("Ваш заказ оформлен!\n\nСостав заказа:\n");
            double total = 0.0;

            for (OrderProduct op : orderProducts) {
                Product p = op.getProduct();
                double sum = p.getPrice() * op.getCountProduct();
                message.append(String.format("- %s x%d: %.2f руб.\n",
                        p.getName(), op.getCountProduct(), sum));
                total += sum;
            }

            message.append(String.format("\nИтого: %.2f руб.", total));
            bot.execute(new SendMessage(chatId, message.toString()));

            ClientOrder newOrder = new ClientOrder();
            newOrder.setClient(client.get());
            newOrder.setStatus(1);
            newOrder.setTotal(0.0);
            entitiesService.saveOrder(newOrder);

            bot.execute(new SendMessage(chatId, "Можете продолжить покупки. Новый заказ создан."));
        } catch (Exception e) {
            log.error("Error during checkout for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка при оформлении заказа");
        }
    }

    private void updateClientPhone(Long chatId, String phone) {
        try {
            Optional<Client> clientOpt = entitiesService.findClientByExternalId(chatId);
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                client.setPhoneNumber(phone);
                entitiesService.saveClient(client);
                bot.execute(new SendMessage(chatId,
                        "Спасибо! Теперь укажите ваш адрес для доставки:"));
            } else {
                bot.execute(new SendMessage(chatId,
                        "Не удалось найти ваш профиль. Пожалуйста, начните с команды /start"));
            }
        } catch (Exception e) {
            log.error("Error updating phone for chatId: " + chatId, e);
            sendErrorMessage(chatId, "Ошибка при сохранении номера телефона");
        }
    }

    private void sendErrorMessage(Long chatId, String message) {
        try {
            if (chatId != null) {
                bot.execute(new SendMessage(chatId, message));
            }
        } catch (Exception e) {
            log.error("Failed to send error message to chatId: " + chatId, e);
        }
    }
}

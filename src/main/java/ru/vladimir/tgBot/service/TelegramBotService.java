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
import org.springframework.stereotype.Service;
import ru.vladimir.tgBot.entity.*;

import java.util.List;
import java.util.Optional;

@Service
public class TelegramBotService {
    private final EntitiesService entitiesService;
    private TelegramBot bot;

    public TelegramBotService(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    @PostConstruct
    public void init() {
        bot = new TelegramBot("8158503770:AAEgDa5E_FL3F3kx2v2grRDFfe09kc_4S3Y");
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processUpdate(Update update) {
        if (update.callbackQuery() != null) {
            processCallbackQuery(update);
        } else if (update.message() != null && update.message().text() != null) {
            processMessage(update);
        }
    }

    private void processCallbackQuery(Update update) {
        Long chatId = update.callbackQuery().message().chat().id();
        String callbackData = update.callbackQuery().data();

        if (callbackData.startsWith("product:")) {
            Long productId = Long.parseLong(callbackData.split(":")[1]);
            addProductToOrder(chatId, productId);
        } else if (callbackData.startsWith("category:")) {
            Long categoryId = Long.parseLong(callbackData.split(":")[1]);
            showCategoryMenu(chatId, categoryId);
        }
    }

    private void processMessage(Update update) {
        Long chatId = update.message().chat().id();
        String text = update.message().text();

        if ("/start".equals(text)) {
            handleStartCommand(chatId, update);
        } else if ("Оформить заказ".equals(text)) {
            handleCheckoutCommand(chatId);
        } else if ("В основное меню".equals(text)) {
            showMainMenu(chatId);
        } else {
            handleCategorySelection(chatId, text);
        }
    }

    private void handleStartCommand(Long chatId, Update update) {
        // Создаем или получаем клиента
        Client client = getOrCreateClient(chatId, update);
        // Создаем новый заказ, если нет активного
        ensureActiveOrder(client);
        // Показываем главное меню
        showMainMenu(chatId);
    }

    private Client getOrCreateClient(Long chatId, Update update) {
        Optional<Client> existingClient = entitiesService.findClientByExternalId(chatId);

        if (existingClient.isPresent()) {
            return existingClient.get();
        } else {
            Client newClient = new Client();
            newClient.setExternalId(chatId);
            newClient.setFullName(update.message().chat().firstName() + " " +
                    update.message().chat().lastName());
            // Можно добавить другие поля из update.message().chat()
            return entitiesService.saveClient(newClient);
        }
    }

    private void ensureActiveOrder(Client client) {
        List<ClientOrder> activeOrders = entitiesService.getClientOrdersByStatus(client.getId(), 1);
        if (activeOrders.isEmpty()) {
            ClientOrder newOrder = new ClientOrder();
            newOrder.setClient(client);
            newOrder.setStatus(1); // "Создан"
            newOrder.setTotal(0.0);
            entitiesService.saveOrder(newOrder);
        }
    }

    private void showMainMenu(Long chatId) {
        List<Category> mainCategories = entitiesService.getCategoriesByParentId(null);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(
                mainCategories.stream()
                        .map(c -> new KeyboardButton(c.getName()))
                        .toArray(KeyboardButton[]::new)
        );
        markup.resizeKeyboard(true);
        markup.addRow(new KeyboardButton("Оформить заказ"));

        bot.execute(new SendMessage(chatId, "Выберите категорию:").replyMarkup(markup));
    }

    private void handleCategorySelection(Long chatId, String categoryName) {
        Optional<Category> category = entitiesService.findCategoryByName(categoryName);
        if (category.isPresent()) {
            showCategoryMenu(chatId, category.get().getId());
        } else {
            bot.execute(new SendMessage(chatId, "Категория не найдена"));
        }
    }

    private void showCategoryMenu(Long chatId, Long categoryId) {
        Category category = entitiesService.getCategoryById(categoryId);
        List<Category> subCategories = entitiesService.getCategoriesByParentId(categoryId);
        List<Product> products = entitiesService.getProductsByCategoryId(categoryId);

        if (!subCategories.isEmpty()) {
            // Показываем подкатегории
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
            // Показываем товары
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
    }

    private void addProductToOrder(Long chatId, Long productId) {
        Optional<Client> client = entitiesService.findClientByExternalId(chatId);
        if (client.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Ошибка: клиент не найден"));
            return;
        }

        List<ClientOrder> activeOrders = entitiesService.getClientOrdersByStatus(client.get().getId(), 1);
        if (activeOrders.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Ошибка: нет активного заказа"));
            return;
        }

        Product product = entitiesService.getProductById(productId);
        if (product == null) {
            bot.execute(new SendMessage(chatId, "Ошибка: товар не найден"));
            return;
        }

        entitiesService.addProductToOrder(activeOrders.get(0).getId(), productId, 1);
        bot.execute(new SendMessage(chatId, "Товар " + product.getName() + " добавлен в заказ"));
    }

    private void handleCheckoutCommand(Long chatId) {
        Optional<Client> client = entitiesService.findClientByExternalId(chatId);
        if (client.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Ошибка: клиент не найден"));
            return;
        }

        List<ClientOrder> activeOrders = entitiesService.getClientOrdersByStatus(client.get().getId(), 1);
        if (activeOrders.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Ошибка: нет активного заказа"));
            return;
        }

        ClientOrder order = activeOrders.get(0);
        List<OrderProduct> orderProducts = entitiesService.getOrderProducts(order.getId());

        if (orderProducts.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Ваш заказ пуст"));
            return;
        }

        // Закрываем заказ
        order.setStatus(2); // "Закрыт"
        entitiesService.saveOrder(order);

        // Формируем сообщение с составом заказа
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

        // Создаем новый заказ
        ClientOrder newOrder = new ClientOrder();
        newOrder.setClient(client.get());
        newOrder.setStatus(1);
        newOrder.setTotal(0.0);
        entitiesService.saveOrder(newOrder);

        bot.execute(new SendMessage(chatId, "Можете продолжить покупки. Новый заказ создан."));
    }
}
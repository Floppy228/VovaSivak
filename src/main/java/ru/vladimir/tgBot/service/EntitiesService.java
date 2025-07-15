package ru.vladimir.tgBot.service;

import ru.vladimir.tgBot.entity.Client;
import ru.vladimir.tgBot.entity.ClientOrder;
import ru.vladimir.tgBot.entity.Product;

import java.util.List;

public interface EntitiesService {
    List<Product> getProductsByCategoryId(Long categoryId);

    List<ClientOrder> getClientOrders(Long clientId);

    List<Product> getClientProducts(Long clientId);

    List<Product> getTopPopularProducts(Integer limit);

    List<Client> searchClientsByName(String name);

    List<Product> searchProducts(String name, Long categoryId);
}

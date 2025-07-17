package ru.vladimir.tgBot.service;

import ru.vladimir.tgBot.entity.*;

import java.util.List;
import java.util.Optional;

public interface EntitiesService {
    List<Product> getProductsByCategoryId(Long categoryId);

    List<ClientOrder> getClientOrders(Long clientId);

    List<Product> getClientProducts(Long clientId);

    List<Product> getTopPopularProducts(Integer limit);

    List<Client> searchClientsByName(String name);

    List<Product> searchProducts(String name, Long categoryId);

    Optional<Client> findClientByExternalId(Long externalId);

    Client saveClient(Client client);

    ClientOrder saveOrder(ClientOrder order);

    List<ClientOrder> getClientOrdersByStatus(Long clientId, Integer status);

    Optional<Category> findCategoryByName(String name);

    Category getCategoryById(Long id);

    List<Category> getCategoriesByParentId(Long parentId);

    Product getProductById(Long id);

    void addProductToOrder(Long orderId, Long productId, Integer quantity);

    List<OrderProduct> getOrderProducts(Long orderId);
}

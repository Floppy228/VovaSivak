package ru.vladimir.tgBot.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.vladimir.tgBot.entity.*;
import ru.vladimir.tgBot.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntitiesServiceImpl implements EntitiesService {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final ClientRepository clientRepo;
    private final ClientOrderRepository orderRepo;
    private final OrderProductRepository orderProductRepo;

    public EntitiesServiceImpl(CategoryRepository categoryRepo,
                               ProductRepository productRepo,
                               ClientRepository clientRepo,
                               ClientOrderRepository orderRepo,
                               OrderProductRepository orderProductRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.clientRepo = clientRepo;
        this.orderRepo = orderRepo;
        this.orderProductRepo = orderProductRepo;
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepo.findAll().stream()
                .filter(product -> product.getCategory() != null && product.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientOrder> getClientOrders(Long clientId) {
        return orderRepo.findAll().stream()
                .filter(order -> order.getClient() != null && order.getClient().getId().equals(clientId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getClientProducts(Long clientId) {
        List<ClientOrder> orders = getClientOrders(clientId);
        return orderProductRepo.findAll().stream()
                .filter(op -> orders.contains(op.getClientOrder()))
                .map(OrderProduct::getProduct)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getTopPopularProducts(Integer limit) {
        return orderProductRepo.findAll().stream()
                .collect(Collectors.groupingBy(OrderProduct::getProduct, Collectors.summingInt(OrderProduct::getCountProduct)))
                .entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> searchClientsByName(String name) {
        String lowerName = name.toLowerCase();
        return clientRepo.findAll().stream()
                .filter(c -> c.getFullName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        String lowerName = name.toLowerCase();
        return productRepo.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }
}

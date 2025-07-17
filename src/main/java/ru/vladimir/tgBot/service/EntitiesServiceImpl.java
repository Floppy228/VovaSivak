package ru.vladimir.tgBot.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vladimir.tgBot.entity.*;
import ru.vladimir.tgBot.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntitiesServiceImpl implements EntitiesService {

    private final ProductRepository productRepo;
    private final ClientRepository clientRepo;
    private final ClientOrderRepository orderRepo;
    private final OrderProductRepository orderProductRepo;
    private final CategoryRepository categoryRepo;

    public EntitiesServiceImpl(ProductRepository productRepo,
                               ClientRepository clientRepo,
                               ClientOrderRepository orderRepo,
                               OrderProductRepository orderProductRepo,
                               CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.clientRepo = clientRepo;
        this.orderRepo = orderRepo;
        this.orderProductRepo = orderProductRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }

    @Override
    public List<ClientOrder> getClientOrders(Long clientId) {
        return orderRepo.findByClientId(clientId);
    }

    @Override
    public List<Product> getClientProducts(Long clientId) {
        return orderProductRepo.findProductsByClientId(clientId);
    }

    @Override
    public List<Product> getTopPopularProducts(Integer limit) {
        return orderProductRepo.findTopPopularProducts(PageRequest.of(0, limit));
    }

    @Override
    public List<Client> searchClientsByName(String name) {
        return clientRepo.findByFullNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> searchProducts(String name, Long categoryId) {
        if (name != null) {
            return productRepo.findByNameContainingIgnoreCase(name);
        } else if (categoryId != null) {
            return productRepo.findByCategoryId(categoryId);
        } else {
            return List.of();
        }
    }

    @Override
    public Optional<Client> findClientByExternalId(Long externalId) {
        return clientRepo.findByExternalId(externalId);
    }

    @Override
    public Client saveClient(Client client) {
        return clientRepo.save(client);
    }

    @Override
    public ClientOrder saveOrder(ClientOrder order) {
        return orderRepo.save(order);
    }

    @Override
    public List<ClientOrder> getClientOrdersByStatus(Long clientId, Integer status) {
        return orderRepo.findByClientIdAndStatus(clientId, status);
    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepo.findByName(name);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id).orElse(null);
    }

    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        if (parentId == null) {
            return categoryRepo.findByParentIsNull()
                    .stream()
                    .filter(c -> !"root".equalsIgnoreCase(c.getName())) // Исключаем категорию "root"
                    .collect(Collectors.toList());
        } else {
            return categoryRepo.findByParentId(parentId);
        }
    }

    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    @Override
    public void addProductToOrder(Long orderId, Long productId, Integer quantity) {
        ClientOrder order = orderRepo.findById(orderId).orElseThrow();
        Product product = productRepo.findById(productId).orElseThrow();

        Optional<OrderProduct> existing = orderProductRepo.findByClientOrderAndProduct(order, product);

        if (existing.isPresent()) {
            OrderProduct op = existing.get();
            op.setCountProduct(op.getCountProduct() + quantity);
            orderProductRepo.save(op);
        } else {
            OrderProduct op = new OrderProduct();
            op.setClientOrder(order);
            op.setProduct(product);
            op.setCountProduct(quantity);
            orderProductRepo.save(op);
        }

        // Обновляем сумму заказа
        double total = orderProductRepo.calculateOrderTotal(orderId);
        order.setTotal(total);
        orderRepo.save(order);
    }

    @Override
    public List<OrderProduct> getOrderProducts(Long orderId) {
        return orderProductRepo.findByClientOrderId(orderId);
    }

}

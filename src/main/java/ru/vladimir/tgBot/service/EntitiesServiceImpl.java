package ru.vladimir.tgBot.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vladimir.tgBot.entity.Client;
import ru.vladimir.tgBot.entity.ClientOrder;
import ru.vladimir.tgBot.entity.Product;
import ru.vladimir.tgBot.repository.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntitiesServiceImpl implements EntitiesService {

    private final ProductRepository productRepo;
    private final ClientRepository clientRepo;
    private final ClientOrderRepository orderRepo;
    private final OrderProductRepository orderProductRepo;

    public EntitiesServiceImpl(ProductRepository productRepo,
                               ClientRepository clientRepo,
                               ClientOrderRepository orderRepo,
                               OrderProductRepository orderProductRepo) {
        this.productRepo = productRepo;
        this.clientRepo = clientRepo;
        this.orderRepo = orderRepo;
        this.orderProductRepo = orderProductRepo;
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

}

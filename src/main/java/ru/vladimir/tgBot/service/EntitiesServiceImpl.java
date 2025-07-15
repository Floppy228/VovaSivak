package ru.vladimir.tgBot.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.vladimir.tgBot.entity.Client;
import ru.vladimir.tgBot.entity.ClientOrder;
import ru.vladimir.tgBot.entity.OrderProduct;
import ru.vladimir.tgBot.entity.Product;
import ru.vladimir.tgBot.repository.*;

import java.util.List;

@Service
@Transactional
public class EntitiesServiceImpl implements EntitiesService {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final ClientRepository clientRepo;
    private final ClientOrderRepository orderRepo;
    private final OrderProductRepository orderProductRepo;

    @PersistenceContext
    private EntityManager entityManager;

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
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.category.id = :categoryId", Product.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<ClientOrder> getClientOrders(Long clientId) {
        return entityManager.createQuery(
                        "SELECT o FROM ClientOrder o WHERE o.client.id = :clientId", ClientOrder.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    @Override
    public List<Product> getClientProducts(Long clientId) {
        return entityManager.createQuery(
                        "SELECT DISTINCT op.product FROM OrderProduct op WHERE op.clientOrder.client.id = :clientId", Product.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    @Override
    public List<Product> getTopPopularProducts(Integer limit) {
        return entityManager.createQuery(
                        "SELECT op.product FROM OrderProduct op GROUP BY op.product ORDER BY SUM(op.countProduct) DESC",
                        Product.class)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<Client> searchClientsByName(String name) {
        return entityManager.createQuery(
                        "SELECT c FROM Client c WHERE LOWER(c.fullName) LIKE LOWER(:name)", Client.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name)", Product.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public List<Product> searchProducts(String name, Long categoryId) {
        if ((name == null || name.isEmpty()) && categoryId == null) {
            return entityManager.createQuery("SELECT p FROM Product p", Product.class)
                    .getResultList();
        }

        if (name != null && !name.isEmpty() && categoryId != null) {
            return entityManager.createQuery(
                            "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name) AND p.category.id = :categoryId", Product.class)
                    .setParameter("name", "%" + name + "%")
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        }

        if (name != null && !name.isEmpty()) {
            return entityManager.createQuery(
                            "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name)", Product.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        }

        // categoryId != null
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.category.id = :categoryId", Product.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

}

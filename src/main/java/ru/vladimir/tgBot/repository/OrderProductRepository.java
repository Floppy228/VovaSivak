package ru.vladimir.tgBot.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.tgBot.entity.ClientOrder;
import ru.vladimir.tgBot.entity.OrderProduct;
import ru.vladimir.tgBot.entity.Product;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "order-products", path = "order-products")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("""
                SELECT DISTINCT op.product
                FROM OrderProduct op
                WHERE op.clientOrder.client.id = :clientId
            """)
    List<Product> findProductsByClientId(@Param("clientId") Long clientId);

    @Query("""
                SELECT op.product
                FROM OrderProduct op
                GROUP BY op.product
                ORDER BY SUM(op.countProduct) DESC
            """)
    List<Product> findTopPopularProducts(Pageable pageable); // 🔧 Добавили Pageable

    @Query("SELECT op FROM OrderProduct op WHERE op.clientOrder.id = :orderId")
    List<OrderProduct> findByClientOrderId(@Param("orderId") Long orderId);

    @Query("SELECT op FROM OrderProduct op WHERE op.clientOrder = :order AND op.product = :product")
    Optional<OrderProduct> findByClientOrderAndProduct(@Param("order") ClientOrder order,
                                                       @Param("product") Product product);

    @Query("SELECT COALESCE(SUM(op.countProduct * op.product.price), 0) " +
            "FROM OrderProduct op WHERE op.clientOrder.id = :orderId")
    Double calculateOrderTotal(@Param("orderId") Long orderId);
}


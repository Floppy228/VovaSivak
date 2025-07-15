package ru.vladimir.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.tgBot.entity.ClientOrder;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "client-orders", path = "client-orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    @Query("SELECT o FROM ClientOrder o WHERE o.client.id = :clientId")
    List<ClientOrder> findByClientId(@Param("clientId") Long clientId);
}

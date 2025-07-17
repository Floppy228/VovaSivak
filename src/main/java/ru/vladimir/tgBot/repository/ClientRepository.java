package ru.vladimir.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.tgBot.entity.Client;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByFullNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT c FROM Client c WHERE c.externalId = :externalId")
    Optional<Client> findByExternalId(@Param("externalId") Long externalId);
}

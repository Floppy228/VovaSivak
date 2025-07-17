package ru.vladimir.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.tgBot.entity.Category;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // В CategoryRepository добавляем:
    List<Category> findByParentIsNull();

    List<Category> findByParentId(Long parentId);

    Optional<Category> findByName(String name);
}

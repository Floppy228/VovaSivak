package ru.vladimir.tgBot;

import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.vladimir.tgBot.entity.Category;
import ru.vladimir.tgBot.entity.Product;
import ru.vladimir.tgBot.repository.CategoryRepository;
import ru.vladimir.tgBot.repository.ProductRepository;

@SpringBootTest
public class FillingTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void fillTestData() {
        Category pizza = new Category();
        pizza.setName("Пицца");
        categoryRepository.save(pizza);

        Category rolls = new Category();
        rolls.setName("Роллы");
        categoryRepository.save(rolls);

        Category burgers = new Category();
        burgers.setName("Бургеры");
        categoryRepository.save(burgers);

        Category drinks = new Category();
        drinks.setName("Напитки");
        categoryRepository.save(drinks);

        // Подкатегории Роллы
        Category classicRolls = new Category();
        classicRolls.setName("Классические роллы");
        classicRolls.setParent(rolls);
        categoryRepository.save(classicRolls);

        Category bakedRolls = new Category();
        bakedRolls.setName("Запеченные роллы");
        bakedRolls.setParent(rolls);
        categoryRepository.save(bakedRolls);

        Category sweetRolls = new Category();
        sweetRolls.setName("Сладкие роллы");
        sweetRolls.setParent(rolls);
        categoryRepository.save(sweetRolls);

        Category rollSets = new Category();
        rollSets.setName("Наборы");
        rollSets.setParent(rolls);
        categoryRepository.save(rollSets);
        
        Product p1 = new Product();
        p1.setName("Ролл Филадельфия");
        p1.setDescription("Лосось, сыр, огурец");
        p1.setPrice(390.0);
        p1.setCategory(classicRolls);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("Ролл Калифорния");
        p2.setDescription("Краб, авокадо");
        p2.setPrice(340.0);
        p2.setCategory(classicRolls);
        productRepository.save(p2);

        Product p3 = new Product();
        p3.setName("Ролл Осака");
        p3.setDescription("Тунец, сыр, огурец");
        p3.setPrice(370.0);
        p3.setCategory(classicRolls);
        productRepository.save(p3);
    }
}

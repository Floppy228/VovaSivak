package ru.vladimir.tgBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;
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
        categoryRepository.deleteAll();
        // --- Родительские категории ---
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

        // --- Подкатегории роллов ---
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

        // --- Товары для Классических роллов ---
        productRepository.save(new Product("Ролл Филадельфия", "Лосось, сыр, огурец", 390.0, classicRolls));
        productRepository.save(new Product("Ролл Калифорния", "Краб, авокадо", 340.0, classicRolls));
        productRepository.save(new Product("Ролл Осака", "Тунец, сыр, огурец", 370.0, classicRolls));

        // --- Товары для Запеченных роллов ---
        productRepository.save(new Product("Ролл Дракон", "Угорь, огурец, майонез", 420.0, bakedRolls));
        productRepository.save(new Product("Ролл Темпура", "Жареный ролл с креветкой", 400.0, bakedRolls));

        // --- Товары для Сладких роллов ---
        productRepository.save(new Product("Ролл Банан", "Банан, шоколад, крем", 290.0, sweetRolls));
        productRepository.save(new Product("Ролл Карамель", "Сыр, карамель, печенье", 310.0, sweetRolls));

        // --- Товары для Наборов ---
        productRepository.save(new Product("Сет Филадельфия+", "Набор из 3 роллов", 980.0, rollSets));
        productRepository.save(new Product("Сет Классика", "6 роллов", 1450.0, rollSets));

        // --- Товары для Пиццы ---
        productRepository.save(new Product("Маргарита", "Сыр, томаты, базилик", 550.0, pizza));
        productRepository.save(new Product("Пепперони", "Пепперони, сыр", 590.0, pizza));
        productRepository.save(new Product("4 Сыра", "Моцарелла, дор блю, чеддер, пармезан", 620.0, pizza));

        // --- Товары для Бургеров ---
        productRepository.save(new Product("Чизбургер", "Говядина, сыр, кетчуп", 330.0, burgers));
        productRepository.save(new Product("Бургер BBQ", "Говядина, лук, BBQ", 360.0, burgers));
        productRepository.save(new Product("Бургер Веган", "Котлета из нута", 310.0, burgers));

        // --- Товары для Напитков ---
        productRepository.save(new Product("Кока-Кола", "Газированный напиток 0.5л", 120.0, drinks));
        productRepository.save(new Product("Фанта", "Газированный напиток 0.5л", 120.0, drinks));
        productRepository.save(new Product("Зеленый чай", "Без сахара, 0.5л", 140.0, drinks));
    }

}

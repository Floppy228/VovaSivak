package ru.vladimir.tgBot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.vladimir.tgBot.entity.*;
import ru.vladimir.tgBot.repository.*;

@SpringBootTest
public class FillingTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void fillCategoriesAndProducts() {
        // 📁 Корневые категории
        Category pizza = categoryRepository.save(new Category("Пицца", null));
        Category rolls = categoryRepository.save(new Category("Роллы", null));
        Category burgers = categoryRepository.save(new Category("Бургеры", null));
        Category drinks = categoryRepository.save(new Category("Напитки", null));

        // 📂 Подкатегории Роллы
        Category classicRolls = categoryRepository.save(new Category("Классические роллы", rolls));
        Category bakedRolls = categoryRepository.save(new Category("Запеченные роллы", rolls));
        Category sweetRolls = categoryRepository.save(new Category("Сладкие роллы", rolls));
        Category rollSets = categoryRepository.save(new Category("Наборы", rolls));

        // 🍔 Подкатегории Бургеры
        Category classicBurgers = categoryRepository.save(new Category("Классические бургеры", burgers));
        Category spicyBurgers = categoryRepository.save(new Category("Острые бургеры", burgers));

        // 🥤 Подкатегории Напитки
        Category soda = categoryRepository.save(new Category("Газированные напитки", drinks));
        Category energy = categoryRepository.save(new Category("Энергетические напитки", drinks));
        Category juice = categoryRepository.save(new Category("Соки", drinks));
        Category other = categoryRepository.save(new Category("Другие", drinks));

        // 🍣 Товары — по 3 для каждой подкатегории
        productRepository.save(new Product(classicRolls, "Ролл Филадельфия", "Лосось, сыр, огурец", 390.0));
        productRepository.save(new Product(classicRolls, "Ролл Калифорния", "Краб, авокадо, майонез", 340.0));
        productRepository.save(new Product(classicRolls, "Ролл Осака", "Тунец, сыр, огурец", 370.0));

        productRepository.save(new Product(bakedRolls, "Ролл Запечённый лосось", "Лосось, сыр, шапка", 420.0));
        productRepository.save(new Product(bakedRolls, "Ролл Терияки", "Курица, соус терияки", 410.0));
        productRepository.save(new Product(bakedRolls, "Ролл BBQ", "Свиные ребрышки, BBQ", 430.0));

        productRepository.save(new Product(sweetRolls, "Ролл с бананом", "Банан, крем, рис", 270.0));
        productRepository.save(new Product(sweetRolls, "Ролл шоколадный", "Шоколад, клубника, сливки", 290.0));
        productRepository.save(new Product(sweetRolls, "Ролл фруктовый", "Яблоко, груша, виноград", 280.0));

        productRepository.save(new Product(rollSets, "Набор Классика", "Лучшие роллы в одном наборе", 890.0));
        productRepository.save(new Product(rollSets, "Набор Острый", "Сеты с перчиком", 910.0));
        productRepository.save(new Product(rollSets, "Набор Премиум", "Филадельфия, сливки, лосось", 950.0));

        productRepository.save(new Product(classicBurgers, "Бургер Классик", "Говядина, сыр, помидор", 280.0));
        productRepository.save(new Product(classicBurgers, "Бургер Веган", "Овощи, соус", 260.0));
        productRepository.save(new Product(classicBurgers, "Бургер Чиз", "Говядина, двойной сыр", 300.0));

        productRepository.save(new Product(spicyBurgers, "Бургер Мексика", "Острый соус, халапеньо", 320.0));
        productRepository.save(new Product(spicyBurgers, "Бургер Огненный", "Соус чили, перец", 330.0));
        productRepository.save(new Product(spicyBurgers, "Бургер Тайский", "Имбирь, острый майонез", 310.0));

        productRepository.save(new Product(soda, "Coca-Cola", "Классическая кола", 100.0));
        productRepository.save(new Product(soda, "Fanta", "Апельсиновый вкус", 95.0));
        productRepository.save(new Product(soda, "Sprite", "Лимон-лайм", 95.0));

        productRepository.save(new Product(energy, "Red Bull", "Энергетик, 250 мл", 130.0));
        productRepository.save(new Product(energy, "Monster", "Энергетик, 500 мл", 150.0));
        productRepository.save(new Product(energy, "Flash", "Энергетик, 250 мл", 120.0));

        productRepository.save(new Product(juice, "Яблочный сок", "100% яблоко", 110.0));
        productRepository.save(new Product(juice, "Апельсиновый сок", "Свежевыжатый апельсин", 120.0));
        productRepository.save(new Product(juice, "Гранатовый сок", "Настоящий гранат", 150.0));

        productRepository.save(new Product(other, "Минеральная вода", "Без газа, 0.5 л", 80.0));
        productRepository.save(new Product(other, "Компот домашний", "Фрукты, сахар", 90.0));
        productRepository.save(new Product(other, "Молочный коктейль", "Молоко, банан, ваниль", 140.0));
    }
}

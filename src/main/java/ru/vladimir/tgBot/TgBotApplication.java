package ru.vladimir.tgBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.vladimir.tgBot")
public class TgBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgBotApplication.class, args);
    }

}

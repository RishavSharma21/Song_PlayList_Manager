package com.example.console;

import com.example.console.ui.MenuHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsoleAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsoleAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(MenuHandler menuHandler) {
        return args -> {
            menuHandler.start();
        };
    }
}
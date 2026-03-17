package ru.yandex.practicum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.yandex.practicum.api")
@SpringBootApplication
public class ShoppingCartRunner {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartRunner.class, args);
    }

}
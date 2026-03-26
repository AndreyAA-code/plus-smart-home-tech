package ru.yandex.practicum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.api.ShoppingStoreFeignClient;

@EnableFeignClients(clients = {ShoppingStoreFeignClient.class})
@SpringBootApplication
public class ShoppingStoreRunner {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingStoreRunner.class, args);
    }

}
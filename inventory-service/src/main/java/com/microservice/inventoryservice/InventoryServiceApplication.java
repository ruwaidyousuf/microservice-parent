package com.microservice.inventoryservice;

import com.microservice.inventoryservice.model.Inventory;
import com.microservice.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}


	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("iphone 13");
			inventory.setQuantity(5);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("iphone se");
			inventory1.setQuantity(0);

			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("hero splender");
			inventory2.setQuantity(200);

			Inventory inventory3 = new Inventory();
			inventory3.setSkuCode("samsung active 2");
			inventory3.setQuantity(25);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);
			inventoryRepository.save(inventory2);
			inventoryRepository.save(inventory3);
		};
	}
}

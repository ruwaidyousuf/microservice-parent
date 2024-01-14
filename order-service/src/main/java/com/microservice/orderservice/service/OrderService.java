package com.microservice.orderservice.service;

import com.microservice.orderservice.dto.InventoryResponse;
import com.microservice.orderservice.dto.OrderLineItemsDTO;
import com.microservice.orderservice.dto.OrderRequest;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.model.OrderLineItems;
import com.microservice.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(orderRequest.getLineItemsDTOList().stream().
                map(this :: mapToDto).toList());

        List<String> skuCodeList = orderRequest.getLineItemsDTOList().stream().
                map(orderLineItem -> orderLineItem.getSkuCode()).toList();

        //calling inventory service to check availability of the product
        InventoryResponse[] inventoryResponseArray = webClient.get().
                uri("http://localhost:8083/api/inventory/getStockInfo",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodeList).build()).
                retrieve().
                bodyToMono(InventoryResponse[].class).
                block(); //to make the call as synchronous

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

        if(allProductsInStock)
            orderRepository.save(order);
        else
            throw new IllegalArgumentException("product is not in stock, please try again later.");
    }

    private OrderLineItems mapToDto(OrderLineItemsDTO orderLineItemsDTO) {
        return OrderLineItems.builder().
                price(orderLineItemsDTO.getPrice()).
                quantity(orderLineItemsDTO.getQuantity()).
                skuCode(orderLineItemsDTO.getSkuCode()).build();
    }

}

package com.Order.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Inventory-Service", url = "http://inventory-service.local:8082/api/v1/reservation")
public interface InventoryInterface {

    @PostMapping("create")
    public void createReservation(@RequestParam Long orderId);

}

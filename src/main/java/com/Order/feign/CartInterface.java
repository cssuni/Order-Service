package com.Order.feign;


import com.Order.model.Cart;
import com.Order.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "Cart-Service", url = "http://localhost:8090/api/v1/cart")
public interface CartInterface {

    @GetMapping("/get")
    public ResponseEntity<Cart> getCart(@RequestParam Long cartId);

    @DeleteMapping("delete")
    public void clearCart(@RequestParam Long cartId);
}

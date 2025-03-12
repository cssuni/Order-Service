package com.Order.controller;


import com.Order.exception.ResourceNotFoundException;
import com.Order.model.Order;
import com.Order.response.ApiResponse;
import com.Order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/Orders")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("create")
    public ResponseEntity<ApiResponse> createOrder(@RequestParam Long cartId) {

        try {
            Order order = orderService.placeOrder(cartId);
            return ResponseEntity.ok(new ApiResponse( "Order Placed Successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }

    }

//    @GetMapping("getUserOrders")
//    public ResponseEntity<ApiResponse> getOrdersByUserId(@RequestParam Long userId){
//        try{
//            return ResponseEntity.ok(new ApiResponse("Orders found successfully", orderService.getOrders(userId)));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
//        }
//    }

    @GetMapping("getOrder/{orderId}")
    public ResponseEntity<ApiResponse> getOrder(@PathVariable Long orderId){
        try{
            return ResponseEntity.ok(new ApiResponse("Order found successfully", orderService.getOrder(orderId)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("orderStatus/{orderId}")
    public ResponseEntity<ApiResponse> orderStatus(@PathVariable Long orderId) {

        try{
            return ResponseEntity.ok(new ApiResponse("Your Order Status is : " + orderService.orderStatus(orderId), null ));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @DeleteMapping("cancel/{orderId}")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId) {

        try{
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Your Order is cancelled :", null));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(),null));
        }

    }




}

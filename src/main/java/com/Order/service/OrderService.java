package com.Order.service;


import com.Order.enums.OrderStatus;
import com.Order.exception.ResourceNotFoundException;
import com.Order.feign.CartInterface;
import com.Order.feign.InventoryInterface;
import com.Order.model.Cart;
import com.Order.model.Order;
import com.Order.model.OrderItem;
import com.Order.repository.OrderItemRepository;
import com.Order.repository.OrderRepository;
import com.Order.response.ApiResponse;
import com.Order.service.redis.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
//    private final CartService cartService;
//    private final CartItemService cartItemService;
    private  final ModelMapper modelMapper;
    private  final OrderItemRepository orderItemRepository;
    private final CartInterface cartInterface;
    private final InventoryInterface inventoryInterface;
    private final RedisService redisService;



    @Override
    public Order placeOrder(Long cartId) {

        Cart cart = cartInterface.getCart(cartId).getBody();

        assert cart != null;

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalAmount());
        order.setOrderStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(cartItem.getItemId());
            orderItem.setProductTitle(cartItem.getProductTitle());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setProductUrl(cartItem.getProductUrl());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            orderItem.setOrder(order);

            return orderItemRepository.save(orderItem);    }).toList();
        order.setOrderItems(orderItems);

        inventoryInterface.createReservation(order.getId());

        return order;
    }

//    @Override
//    public List<Order> getOrders(Long userId) {
//
//        return orderRepository.findByUserId(userId);
//    }

    @Override
    public Order getOrder(Long orderId) {

        System.out.println("Get Order was called");

        Order order = redisService.getOrderFromRedis(orderId);



        if(order == null)  return orderRepository.findById(orderId)
                .map(redisService::saveOrderInRedis)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

        return order;
    }


    @Override
    public String orderStatus(Long orderId) {

        Order order = redisService.getOrderFromRedis(orderId);
        if(order == null)  return orderRepository.findById(orderId).stream()
                .map(redisService::saveOrderInRedis).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"))
                .getOrderStatus().toString();
        return order.getOrderStatus().toString();
    }

    @Override
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
        order.setOrderStatus(OrderStatus.CANCELLED);
        redisService.saveOrderInRedis(orderRepository.save(order));

    }

}

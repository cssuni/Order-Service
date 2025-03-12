package com.Order.service;


import com.Order.enums.OrderStatus;
import com.Order.exception.ResourceNotFoundException;
import com.Order.feign.CartInterface;
import com.Order.model.Cart;
import com.Order.model.Order;
import com.Order.model.OrderItem;
import com.Order.repository.OrderItemRepository;
import com.Order.repository.OrderRepository;
import com.Order.response.ApiResponse;
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



    @Override
    public Order placeOrder(Long cartId) {

        Cart cart = cartInterface.getCart(cartId).getBody();

        assert cart != null;

        System.out.println(cart);

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
        cartInterface.clearCart(cart.getId());

        return order;
    }

//    @Override
//    public List<Order> getOrders(Long userId) {
//
//        return orderRepository.findByUserId(userId);
//    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
    }


    @Override
    public String orderStatus(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

        return order.getOrderStatus().toString();
    }

    @Override
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

    }
}

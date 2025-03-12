package com.Order.service;



import com.Order.model.Order;

import java.util.List;

public interface IOrderService {

    Order placeOrder(Long userId);

    Order getOrder(Long orderId);

    String orderStatus(Long orderId);
    void cancelOrder(Long orderId);

}

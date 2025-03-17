package com.Order.service.redis;

import com.Order.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    public void saveData(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }


    public Order saveOrderInRedis(Order order){

        try{
            String userDtoJson = objectMapper.writeValueAsString(order);
            redisTemplate.opsForValue().set("order:"+order.getId().toString(),userDtoJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    public Order getOrderFromRedis(Long id){
        String productDtoJson = (String)redisTemplate.opsForValue().get("order:"+id.toString());

        if(productDtoJson != null) {
            try {
                return objectMapper.readValue(productDtoJson, Order.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

}

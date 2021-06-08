package com.Redis.dao;

import com.Redis.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDao {
    void add(Order order);
    List<Order> successinfo();
    List<Order> failureinfo();
}

package com.Redis.dao;

import com.Redis.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao {
    void add(Order order);
}

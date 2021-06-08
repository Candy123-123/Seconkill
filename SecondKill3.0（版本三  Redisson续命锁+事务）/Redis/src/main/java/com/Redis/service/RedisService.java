package com.Redis.service;


import com.Redis.entity.CommonResult;
import com.Redis.entity.Order;

import java.util.List;

public interface RedisService {
    //设置已经购买成功的用户的信息
    public CommonResult setKey();
    //查看购买成功的用户的信息
    public CommonResult successinfo();
    public CommonResult failureinfo();
    //向数据库中插入数据
    public void insertOrder(List<Order> list);
    //向数据库中插入数据
    public void insertSingleOrder(Order order);

}

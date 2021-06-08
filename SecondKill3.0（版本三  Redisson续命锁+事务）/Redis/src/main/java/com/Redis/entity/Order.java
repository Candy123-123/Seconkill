package com.Redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    private String id;//主键
    private String userId;//用户id
    private Integer productId;//商品id
    private Integer count;//抢购数量
    private Integer status; //订单状态：0：创建中；1：已完结
}


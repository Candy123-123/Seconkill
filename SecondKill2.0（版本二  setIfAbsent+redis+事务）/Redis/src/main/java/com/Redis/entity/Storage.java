package com.Redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Storage {
    private Integer id;//主键id自增长
    private Integer productId;//商品id
    private Integer storage;//原始库存
    private Integer used;//已卖出
    private Integer rest;//剩余
}

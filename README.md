# Seconkill
最终整体逻辑
![image](https://user-images.githubusercontent.com/57355730/121180060-fff5a080-c892-11eb-88f7-984862f4c084.png)


## 项目运行步骤

版本1忽略，记得把redis打开即可

版本2.0 以后需要看这个步骤

### 一 建库建表

#### 1.1 secondkill

```sql
create database secondkill;
```

#### 1.2 Order表

```sql
#字段的意思：id 用户id 商品id 秒杀数量 订单状态
CREATE TABLE `sk_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(200) NOT NULL,
  `productId` int(10) NOT NULL,
  `count` int(10) NOT NULL,
  `status` int(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

#### 1.3 Storage表

```sql
#字段的意思：id 商品id 库存 已经卖出的数量 剩余库存
CREATE TABLE `sk_storage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL,
  `storage` int(11) NOT NULL,
  `used` int(11) NOT NULL,
  `rest` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

#### 1.4 Storage表中插入商品信息

```sql
#程序里面写死了 秒杀的是商品id为2的商品这里必须加上
INSERT INTO `secondkill`.`sk_storage` (`id`, `productId`, `storage`, `used`, `rest`) VALUES ('1', '2', '1000', '0', '1000');
```

### 二  更改yml文件

```yml
#注意 redis和datasource设置为你自己的参数！

server:
  port: 80


spring:
  application:
    name: redis-test
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/secondkill?useUnicode=true&characterEncoding=utf-8&useSSL=false
    username: root
    password: root

  redis:
    host: 127.0.0.1
    port: 6379
    password:
    jedis:
      pool:
        max-active: 8
        max-wait: -1
        max-idle: 500
        min-idle: 0
    lettuce:
      shutdown-timeout: 0

mybatis:
  mapperLocations: classpath:mapper/*.xml
  type-aliases-package: com.Redis.entity    # 所有Entity别名类所在包
```

### 三 启动

```
1 等maven下载完依赖
2 先打开Redis
3 运行主启动即可
```

### 四 测试接口

```yml
下订单 localhost/redis/set
获取成功秒杀的用户信息 localhost/redis/get/failure 版本4不可用因为把缓存清空了
获取秒杀失败的用户信息 localhost/redis/get/success 版本4不可用因为把缓存清空了
```


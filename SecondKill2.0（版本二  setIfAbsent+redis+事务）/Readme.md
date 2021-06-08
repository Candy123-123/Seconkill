```java
1 redis缓存方案
	把请求订单放在redis的list中，当list的容量达到秒杀商品数量时，list中不再放数据，开始先数据库中同步数据，插入订单，减库存
2 高并发处理方案
	设置一个redis锁，set（key，value），如果key存在，就返回false说明没办法获取锁，返回true说明没有锁
	//尝试获取锁
    Boolean mylock=lock.opsForValue().setIfAbsent(lockKey,lockValue,30,TimeUnit.SECONDS);
3 事务
    springboot的事务注解
```


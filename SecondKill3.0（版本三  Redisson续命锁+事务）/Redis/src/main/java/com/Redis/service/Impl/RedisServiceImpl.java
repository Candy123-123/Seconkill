package com.Redis.service.Impl;
import com.Redis.dao.OrderDao;
import com.Redis.dao.StorageDao;
import com.Redis.entity.CommonResult;
import com.Redis.entity.Order;
import com.Redis.entity.Storage;
import com.Redis.service.RedisService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class RedisServiceImpl implements RedisService {
    @Resource
    private ListOperations<String, Order> redisTemplate;

    private Order order;

    @Resource
    private OrderDao orderDao;

    @Resource
    private StorageDao storageDao;

    @Resource
    private Redisson redisson;

    @Override
    public  CommonResult setKey() {
        String lockKey = "testStock";
        RLock lock = redisson.getLock(lockKey);
        lock.lock(30, TimeUnit.SECONDS);
        try{
            order=new Order();
            String id= UUID.randomUUID().toString();
            order.setId(id);
            order.setCount(1);
            order.setProductId(2);
            order.setStatus(0);
            order.setUserId(id);
            System.out.println(redisTemplate.size("success1") +" "+ redisTemplate.size("failure1"));
            if(redisTemplate.size("success1")<200){
                redisTemplate.leftPush("success1",order);
                return new CommonResult(200,"恭喜您秒杀成功！",order);
            }else{
                //向数据库进行提交
                if(redisTemplate.size("success1")==200&&redisTemplate.size("failure1")==0){
                    insertOrder(redisTemplate.range("success1",0,redisTemplate.size("success1")-1));
                }
                redisTemplate.leftPush("failure1",order);
                return new CommonResult(444,"很抱歉,商品秒杀完毕",order);
            }
        }finally{
            lock.unlock();
        }

    }

    @Override
    public CommonResult successinfo() {
        List<Order> list1= redisTemplate.range("success1",0,redisTemplate.size("success1")-1);
        return new CommonResult(200,list1.size()+"个人秒杀成功",list1);
    }

    @Override
    public CommonResult failureinfo() {
        List<Order> list2= redisTemplate.range("failure1",0,redisTemplate.size("failure1")-1);
        return new CommonResult(200,list2.size()+"个人秒杀失败",list2);
    }


    /**
     * 批量插入订单
     * @param list
     */
    @Override
    @Transactional
    public void insertOrder(List<Order> list) {
        int sum=0;
        for(Order i:list){
            sum+=i.getCount();
            orderDao.add(i);
        }
        Storage storage=new Storage();
        storage.setProductId(list.get(0).getProductId());
        storage.setUsed(sum);
        storageDao.update(storage);
    }

    /**
     * 插入一条数据
     * @param order
     */
    @Override
    public void insertSingleOrder(Order order) {
        orderDao.add(order);
    }
}

package com.Redis.service.Impl;
import com.Redis.dao.OrderDao;
import com.Redis.dao.StorageDao;
import com.Redis.entity.CommonResult;
import com.Redis.entity.Order;
import com.Redis.entity.SecondKillTime;
import com.Redis.entity.Storage;
import com.Redis.service.RedisService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@EnableScheduling//支持定时任务类
public class RedisServiceImpl implements RedisService {
    @Resource
    private ListOperations<String, Order> redisTemplate;

    @Resource
    private RedisTemplate<String,String> manage;

    private Order order;

    @Resource
    private OrderDao orderDao;

    @Resource
    private StorageDao storageDao;

    @Resource
    private Redisson redisson;

    //是否开启秒杀的标志位
    private static Boolean isStart=true;

    //库存信息
    private static Integer STORAGE_NUM=-1;

    @Override
    public  CommonResult setKey() {
        if(STORAGE_NUM==-1){
            Storage s=new Storage();
            s.setProductId(2);
            STORAGE_NUM=storageDao.readStorage(s)/5;
        }
        Calendar cal=Calendar.getInstance();
        cal.setTime(new Date());
        if(isStart&&cal.compareTo(SecondKillTime.STARTTIME.getCalendar())>0&&cal.compareTo(SecondKillTime.ENDTIME.getCalendar())<0){
            String lockKey = "Redis_Order_Lock";
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
                if(redisTemplate.size("success1")<STORAGE_NUM){
                    order.setStatus(1);
                    redisTemplate.leftPush("success1",order);
                    return new CommonResult(200,"恭喜您秒杀成功！O(∩_∩)O",order);
                }else{
                    redisTemplate.leftPush("failure1",order);
                    return new CommonResult(444,"很抱歉,商品秒杀完毕，您没有抢到 ┭┮﹏┭┮",order);
                }
            }finally{
                lock.unlock();
            }
        }else{
            return new CommonResult(444,"很抱歉,秒杀时间已过或者商品已经抢光！活动结束");
        }


    }

    @Override
    public CommonResult successinfo() {
        List<Order> success= orderDao.successinfo();
        return new CommonResult(200,success.size()+"个人秒杀成功",success);
        //List<Order> list1= redisTemplate.range("success1",0,redisTemplate.size("success1")-1);
        //return new CommonResult(200,list1.size()+"个人秒杀成功",list1);

    }

    @Override
    public CommonResult failureinfo() {
        List<Order> failure= orderDao.failureinfo();
        return new CommonResult(200,failure.size()+"个人秒杀失败",failure);
        //List<Order> list2= redisTemplate.range("failure1",0,redisTemplate.size("failure1")-1);
        //return new CommonResult(200,list2.size()+"个人秒杀失败",list2);
    }


    /**
     * //提交的两个条件 满足其一就可以
     *  //1 order订单满了
     *  //2 秒杀时间结束并且order订单还没有提交过
     * 批量插入订单
     */
    @Override
    @Transactional
    @Scheduled(cron = "0/1 * * * * ?")
    public void insertOrder() {
        String lockKey = "DAO_Lock";
        RLock lock = redisson.getLock(lockKey);
        lock.lock(30, TimeUnit.SECONDS);
        try{
            List<Order> list=redisTemplate.range("success1",0,redisTemplate.size("success1")-1);
            List<Order> list1=redisTemplate.range("failure1",0,redisTemplate.size("success1")-1);
            Calendar cal=Calendar.getInstance();
            cal.setTime(new Date());
            //提交的两个条件 满足其一就可以
            //1 order订单满了
            //2 秒杀时间结束并且order订单还没有提交过
            if(list.size()==STORAGE_NUM||(cal.compareTo(SecondKillTime.ENDTIME.getCalendar())>0)){
                isStart=false;//禁止往redis中添加东西了
                if(list.size()!=0){
                    int sum=0;
                    //添加成功order信息
                    for(Order i:list){
                        sum+=i.getCount();
                        orderDao.add(i);
                    }
                    //修改库存信息
                    Storage storage=new Storage();
                    storage.setProductId(list.get(0).getProductId());
                    storage.setUsed(sum);
                    //添加失败order信息
                    for(Order o:list1){
                        orderDao.add(o);
                    }
                    //清空缓存信息
                    storageDao.update(storage);
                    manage.delete("success1");//清空成功秒杀的信息
                    manage.delete("failure1");//清空失败信息
                }

            }
        }finally{
            lock.unlock();
        }

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

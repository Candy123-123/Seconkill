

## 一  用日期枚举表示开始和结束时间

```java
package com.Redis.entity;
import io.netty.channel.ChannelPromiseAggregator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public enum SecondKillTime {
    STARTTIME(2021,5,8,10,4,0),
    ENDTIME(2021,5,8,11,30,00);
    private final Calendar cal;
    private SecondKillTime(Integer a0,Integer a1,Integer a2,Integer a3,Integer a4,Integer a5){
        this.cal=Calendar.getInstance();
        this.cal.set(a0,a1,a2,a3,a4,a5);
    }
    public Calendar getCalendar(){
        return cal;
    }
}
```

## 二 将往数据库中插数据的方法 和 往redis中放缓存的方法分开

![image-20210608164531109](D:\学习笔记\刷题笔记截图\image-20210608164531109.png)

### 2.1 往Redis中放缓存的方法

判断条件

1 在秒杀时间内 并且 商品没有秒杀完毕 可以进入该方法

```java
@Override
    public  CommonResult setKey() {
        Calendar cal=Calendar.getInstance();
        cal.setTime(new Date());
        if(isStart&&cal.compareTo(SecondKillTime.STARTTIME.getCalendar())>0&&cal.compareTo(SecondKillTime.ENDTIME.getCalendar())<0){
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
```

### 2.2 往数据库中插数据的方法 

判断条件

商品秒杀完毕 或者 秒杀时间到 结束秒杀 清空Redis缓存同时禁止redis中插入数据

实现思路：Spring Schedule注解

```java
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
        String lockKey = "date_insert";
        RLock lock = redisson.getLock(lockKey);
        lock.lock(30, TimeUnit.SECONDS);
        try{
            List<Order> list=redisTemplate.range("success1",0,redisTemplate.size("success1")-1);
            Calendar cal=Calendar.getInstance();
            cal.setTime(new Date());
            //提交的两个条件 满足其一就可以
            //1 order订单满了
            //2 秒杀时间结束并且order订单还没有提交过
            if(list.size()>=200||(cal.compareTo(SecondKillTime.ENDTIME.getCalendar())>0)){
                isStart=false;//禁止往redis中添加东西了
                if(list.size()!=0){
                    int sum=0;
                    for(Order i:list){
                        sum+=i.getCount();
                        orderDao.add(i);
                    }
                    Storage storage=new Storage();
                    storage.setProductId(list.get(0).getProductId());
                    storage.setUsed(sum);
                    storageDao.update(storage);
                    manage.delete("success1");//清空成功秒杀的信息
                    manage.delete("failure1");//清空失败信息
                }

            }
        }finally{
            lock.unlock();
        }

    }
```


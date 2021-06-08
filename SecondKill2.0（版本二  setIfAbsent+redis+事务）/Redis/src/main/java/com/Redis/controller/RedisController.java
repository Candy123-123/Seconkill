package com.Redis.controller;
import com.Redis.entity.CommonResult;
import com.Redis.entity.Order;
import com.Redis.service.Impl.RedisServiceImpl;
import com.Redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;


@RestController
@Slf4j
public class RedisController {

    @Resource
    private RedisService redisService;

    /**
     * 下订单
     * @return
     */
    @GetMapping("/redis/set")
    public CommonResult setKey(){
        return redisService.setKey();
    }

    /**
     * 获取成功秒杀的信息
     * @return
     */
    @GetMapping("/redis/get/success")
    public CommonResult getSuccess(){
        return redisService.successinfo();

    }

    /**
     * 测试方法
     * 获取秒杀失败的信息
     * @return
     */
    @GetMapping("/redis/get/failure")
    public CommonResult getFailure(){
        return redisService.failureinfo();

    }

    /**
     * 测试往数据库中插入一条数据
     */
    @GetMapping("/redis/test")
    public CommonResult Test(){
        Order order=new Order();
        String id= UUID.randomUUID().toString();
        order.setId(id);
        order.setCount(1);
        order.setProductId(2);
        order.setStatus(0);
        order.setUserId(id);
        redisService.insertSingleOrder(order);
        return new CommonResult(200,"下订单成功");
    }


}

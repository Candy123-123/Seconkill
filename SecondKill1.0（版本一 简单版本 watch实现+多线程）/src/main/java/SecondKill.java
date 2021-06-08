
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * 采用多线程的方式展示一个1000人秒杀100部手机的实例，实现秒杀功能
 * <p>
 * 创建多线程，并利用Redis事务功能，实现秒杀功能
 */
public class SecondKill implements Runnable {

    String iPhone = "iPhone";
    //创建jedis实例
    Jedis jedis = new Jedis("127.0.0.1", 6379);

    String userinfo;

    public SecondKill() {

    }

    public SecondKill(String userinfo) {
        this.userinfo = userinfo;
    }

    @Override
    public void run() {
        try {
            //使用watch命令监控iPhone键
            jedis.watch(iPhone);

            String val = jedis.get(iPhone);
            Integer valint = Integer.valueOf(val);
            if (valint <= 100 && valint >= 1) {
                //使用multi命令开启事务
                Transaction tx = jedis.multi();
                //事物命令入队
                tx.incrBy("iPhone", -1);
                //使用exec命令执行事务
                //提交事务，如果此时watchkeys被改动了，则返回null
                List<Object> list = tx.exec();

                if (list == null || list.size() == 0) {
                    String failuserinfo = "fail" + userinfo;
                    String failinfo = "用户：" + failuserinfo + "商品争抢失败，抢购失败";
                    System.out.println(failinfo);
                    /*抢购失败业务逻辑*/
                    jedis.setnx(failuserinfo, failinfo);
                } else {
                    for (Object succ : list) {
                        String succuserinfo = "succ" + succ.toString() + userinfo;
                        String succinfo = "用户：" + succuserinfo + "抢购成功，当前抢购人数：" + (1 - (valint - 100));
                        System.out.println(succinfo);
                        /*抢购成功业务逻辑*/
                        jedis.setnx(succuserinfo, succinfo);
                    }
                }

            } else {
                String failuserinfo = "kcfail" + userinfo;
                String failinfo1 = "用户：" + failuserinfo + "商品抢购完毕，抢购失败";
                System.out.println(failinfo1);
                jedis.setnx(failuserinfo, failinfo1);
                //Thread.sleep(500);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }


    }
}



import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Redis秒杀功能实现，1000人抢购100部手机
 */
public class SecondKillTest {

    public static void main(String[] args) {
        final String iPhone = "iPhone";

        //20个线程池并发数
        ExecutorService executor = Executors.newFixedThreadPool(20);

        final Jedis jedis = new Jedis("127.0.0.1", 6379);
        //先删除
        jedis.del(iPhone);
        //设置起始的抢购数
        jedis.set(iPhone, "100");

        jedis.close();
        //设置1000人的抢购
        for (int i = 0; i < 1000; i++) {
            executor.execute(new SecondKill("user"+getRandomString(6)));
        }
        executor.shutdown();
    }

    /**
     * 生成用户ID
     *
     * @param length 随机字符串长度
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}




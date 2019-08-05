package io.github.xiaoyureed.redispractice;

import io.github.xiaoyureed.redispractice.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisPracticeApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        final ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set("key", "value");
        final String result = stringStringValueOperations.get("key");
        System.out.println(result);
    }

    @Test
    public void testDateUtils() {
        final String format = DateUtils.format(LocalDateTime.now());
        System.out.println(format);
        final LocalDateTime localDateTime = DateUtils.deFormat(format);
        System.out.println(localDateTime);
    }

    @Test
    public void contextLoads() {
        final String hello = stringRedisTemplate.opsForValue().get("hello");
        System.out.println(hello);
        stringRedisTemplate.delete("hello");
    }

}

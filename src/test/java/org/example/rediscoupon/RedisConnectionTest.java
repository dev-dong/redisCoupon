package org.example.rediscoupon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisConnectionTest {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired // 생성자 주입
    public RedisConnectionTest(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Test
    public void testRedisConnection() {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String key = "testKey";
        String value = "Hello, Redis!";

        valueOps.set(key, value);
        String fetchedValue = valueOps.get(key);

        assertThat(fetchedValue).isEqualTo(value);
        redisTemplate.delete(key);
    }
}

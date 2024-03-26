package org.example.rediscoupon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    public void flushRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    public void 한명응모() {
        Long userId = 1L;
        String type = "book";
        couponService.applyCoupon(userId, type);
        Long count = redisTemplate.opsForList().size("coupons:" + type);
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 여러명응모() throws InterruptedException {
        int threadCount = 1000;
        String type = "book";
        IntStream.range(0, threadCount).parallel().forEach(i -> couponService.applyCoupon((long) i, type));

        // 검증
        Long count = redisTemplate.opsForList().size("coupons:" + type);
        assertThat(count).isEqualTo(100);
    }
}

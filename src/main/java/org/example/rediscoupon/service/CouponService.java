package org.example.rediscoupon.service;

import lombok.extern.slf4j.Slf4j;
import org.example.rediscoupon.repository.CouponCountRepository;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class CouponService {

    private final RedisTemplate<String, String> redisTemplate;

    private final CouponCountRepository couponCountRepository;

    public CouponService(RedisTemplate<String, String> redisTemplate, CouponCountRepository couponCountRepository) {
        this.redisTemplate = redisTemplate;
        this.couponCountRepository = couponCountRepository;
    }

    public void applyCoupon(Long userId, String type) {
        Long count = couponCountRepository.increment(type);

        if (count > 100) return;

        String couponId = UUID.randomUUID().toString();
        String key = "coupon:" + couponId;
        String userCouponKey = "user:coupons:" + userId;


        // 해시에 쿠폰 정보 저장
        redisTemplate.opsForHash().put(key, "type", type);
        redisTemplate.opsForHash().put(key, "discount_rate", "10%");
        redisTemplate.expire(key, Duration.ofDays(30));

        // type별로 쿠폰을 List에 추가
        redisTemplate.opsForList().rightPush("coupons:" + type, key);

        // userID에 couponID를 SET에 추가
        redisTemplate.opsForSet().add(userCouponKey, key);
    }
}

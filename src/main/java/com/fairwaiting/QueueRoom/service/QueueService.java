package com.fairwaiting.QueueRoom.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class QueueService
{
    private final StringRedisTemplate redisTemplate;  // This is the built-in Spring class that directly talks to the Redis container you just started.


    private static final String QUEUE_KEY = "waiting_room_zset";

    public QueueService(StringRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    public boolean joinQueue(String userId)
    {
        long timestamp = Instant.now().toEpochMilli();

        Boolean isAdded = redisTemplate.opsForZSet().add(QUEUE_KEY,userId,timestamp);

        return isAdded != null && isAdded;
    }

    public Long getUserPosition(String userId)
    {
        return redisTemplate.opsForZSet().rank(QUEUE_KEY , userId);
    }
}

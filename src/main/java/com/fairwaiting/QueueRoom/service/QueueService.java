package com.fairwaiting.QueueRoom.service;

import com.fairwaiting.QueueRoom.entity.AdmissionRecord;
import com.fairwaiting.QueueRoom.repository.AdmissionRecordRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class QueueService
{
    private final StringRedisTemplate redisTemplate;  // This is the built-in Spring class that directly talks to the Redis container you just started.

    private final AdmissionRecordRepository admissionRecordRepository;

    private static final String QUEUE_KEY = "waiting_room_zset";

    public QueueService(StringRedisTemplate redisTemplate, AdmissionRecordRepository admissionRecordRepository)
    {
        this.redisTemplate = redisTemplate;
        this.admissionRecordRepository = admissionRecordRepository;
    }

    public Boolean joinQueue(String userId)
    {
        long timestamp = Instant.now().toEpochMilli();

        Boolean isAdded = redisTemplate.opsForZSet().addIfAbsent(QUEUE_KEY,userId,timestamp);

        return isAdded != null && isAdded;
    }

    public Long getQueueSize()
    {
        return redisTemplate.opsForZSet().zCard(QUEUE_KEY);
    }

    public Long getUserPosition(String userId)
    {
        return redisTemplate.opsForZSet().rank(QUEUE_KEY , userId);
    }

    public AdmissionRecord admitNextuser()
    {
        ZSetOperations.TypedTuple<String> tuple = redisTemplate.opsForZSet().popMin(QUEUE_KEY);

        if (tuple == null || tuple.getValue() == null)
        {
            return  null;
        }

        String userId = tuple.getValue();

        AdmissionRecord record = new AdmissionRecord();
        record.setUserId(userId);
        record.setStatus("ADMITTED");
        record.setAdmittedAt(new Date().toInstant());

        return admissionRecordRepository.save(record);

    }
}

package com.fairwaiting.QueueRoom.service;

import com.fairwaiting.QueueRoom.entity.AdmissionRecord;
import com.fairwaiting.QueueRoom.kafka.QueueEventProducer;
import com.fairwaiting.QueueRoom.kafka.event.EventType;
import com.fairwaiting.QueueRoom.kafka.event.QueueEvent;
import com.fairwaiting.QueueRoom.repository.AdmissionRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class QueueService
{
    private final StringRedisTemplate redisTemplate;  // This is the built-in Spring class that directly talks to the Redis container you just started.
    private final AdmissionRecordRepository admissionRecordRepository;
    private final QueueEventProducer eventProducer;

    private static final String QUEUE_KEY = "waiting_room_zset";
    private static final String USER_JOINED_TOPIC = "user-joined";

    public QueueService(StringRedisTemplate redisTemplate, AdmissionRecordRepository admissionRecordRepository, QueueEventProducer eventProducer)
    {
        this.redisTemplate = redisTemplate;
        this.admissionRecordRepository = admissionRecordRepository;
        this.eventProducer = eventProducer;
    }

    public Boolean joinQueue(String userId)
    {
        long timestamp = Instant.now().toEpochMilli();

        Boolean isAdded = redisTemplate.opsForZSet().addIfAbsent(QUEUE_KEY,userId,timestamp);
        boolean isNew = isAdded != null && isAdded;

        if(isNew)
        {
            publishJoinedEvent(userId,timestamp);
        }

        return isNew;
    }

    private void publishJoinedEvent(String userId , long joinedAt)
    {
        QueueEvent event = QueueEvent.builder()
                .eventId("JOINED:"+userId+":"+joinedAt)
                .type(EventType.JOINED)
                .userId(userId)
                .occurredAt(Instant.ofEpochMilli(joinedAt))
                .build();

        try
        {
            eventProducer.publishEvent(USER_JOINED_TOPIC , event);
        }
        catch (Exception e)
        {
            log.error("Could not publish user joined event for {}",userId, e);
        }

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

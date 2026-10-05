package com.fairwaiting.QueueRoom.service;

import com.fairwaiting.QueueRoom.entity.AdmissionRecord;
import com.fairwaiting.QueueRoom.repository.AdmissionRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AdmissionService
{
    private final AdmissionRecordRepository repository;

    public AdmissionService(AdmissionRecordRepository repository)
    {
        this.repository = repository;
    }

    public AdmissionRecord createTestRecord(String userId)
    {
        AdmissionRecord record = new AdmissionRecord();

        record.setUserId(userId);
        record.setStatus("ADMITTED");
        record.setAdmittedAt(Instant.now());

        return repository.save(record);
    }
}

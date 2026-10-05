package com.fairwaiting.QueueRoom.repository;

import com.fairwaiting.QueueRoom.entity.AdmissionRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionRecordRepository extends MongoRepository<AdmissionRecord , String>
{

}

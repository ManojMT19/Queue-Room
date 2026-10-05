package com.fairwaiting.QueueRoom.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data  // this is from lombok which help in generating getters n setters automatically in background  so we dont have to type them
@Document(collection = "admission_records")
public class AdmissionRecord
{
    @Id
    private String Id;

    private String userId;
    private String status;
    private Instant admittedAt;

}

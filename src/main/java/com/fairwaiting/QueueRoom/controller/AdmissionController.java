package com.fairwaiting.QueueRoom.controller;

import com.fairwaiting.QueueRoom.entity.AdmissionRecord;
import com.fairwaiting.QueueRoom.service.AdmissionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionController
{
    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService)
    {
        this.admissionService = admissionService;
    }

    @PostMapping("/test")
    public AdmissionRecord testCreate(@RequestParam String userId)
    {
        return admissionService.createTestRecord(userId);
    }
}

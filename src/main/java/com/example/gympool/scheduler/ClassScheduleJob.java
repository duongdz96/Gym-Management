package com.example.gympool.scheduler;

import com.example.gympool.service.ClassScheduleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClassScheduleJob {

    private final ClassScheduleService classScheduleService;

    public ClassScheduleJob(ClassScheduleService classScheduleService) {
        this.classScheduleService = classScheduleService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void autoCloseSchedules() {
        System.out.println("Bắt đầu quét lịch học hết hạn...");
        classScheduleService.scanAndCloseExpiredSchedules();
        System.out.println("Kết thúc quét lịch học.");
    }
}
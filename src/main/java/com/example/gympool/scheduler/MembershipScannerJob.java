package com.example.gympool.scheduler;

import com.example.gympool.service.CustomerMembershipService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MembershipScannerJob {

    private final CustomerMembershipService customerMembershipService;

    public MembershipScannerJob(CustomerMembershipService customerMembershipService) {
        this.customerMembershipService = customerMembershipService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void runJob() {
        System.out.println("Job start...");
        customerMembershipService.scanAndExpireMemberships();
        System.out.println("Job end.");
    }
}
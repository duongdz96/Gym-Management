package com.example.gympool.service;

import com.example.gympool.entity.MemberRegistration;
import com.example.gympool.entity.PTAppointment;
import com.example.gympool.repository.MemberRegistrationRepository;
import com.example.gympool.repository.PTAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final PTAppointmentRepository ptAppointmentRepository;
    private final MemberRegistrationRepository memberRegistrationRepository; // Thêm vào đây
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 300000)
    public void checkAndSendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusMinutes(30);
        processPTReminders(now, windowEnd);
        processClassReminders(now, windowEnd);
    }

    private void processPTReminders(LocalDateTime start, LocalDateTime end) {
        List<PTAppointment> appointments = ptAppointmentRepository.findUpcomingAppointments(start, end);
        for (PTAppointment appointment : appointments) {
            try {
                notificationService.sendAppointmentReminder(appointment);
                appointment.setNotificationSent(true);
                ptAppointmentRepository.save(appointment);
            } catch (Exception e) {
                System.err.println("Lỗi gửi mail PT: " + appointment.getId());
            }
        }
    }

    private void processClassReminders(LocalDateTime start, LocalDateTime end) {
        List<MemberRegistration> registrations = memberRegistrationRepository.findUpcomingClassRegistrations(start, end);
        for (MemberRegistration reg : registrations) {
            try {
                notificationService.sendClassReminder(reg);
                reg.setNotificationSent(true);
                memberRegistrationRepository.save(reg);
            } catch (Exception e) {
                System.err.println("Lỗi gửi mail lớp học: " + reg.getId());
            }
        }
    }
}
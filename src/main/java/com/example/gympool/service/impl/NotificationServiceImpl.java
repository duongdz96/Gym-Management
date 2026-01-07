package com.example.gympool.service.impl;

import com.example.gympool.entity.MemberRegistration;
import com.example.gympool.entity.PTAppointment;
import com.example.gympool.service.EmailService;
import com.example.gympool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context; // Lưu ý import đúng package của Thymeleaf
import org.thymeleaf.spring6.SpringTemplateEngine; // Hoặc spring5 tùy version

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    @Override
    public void sendAppointmentReminder(PTAppointment appointment) {
        sendEmailWithTemplate(
                appointment.getPtPackageIssued().getMember().getEmail(),
                appointment.getPtPackageIssued().getMember().getFullName(),
                "Nhắc nhở lịch tập PT",
                "Bạn có lịch hẹn tập luyện 1-1 sắp diễn ra.",
                "Tập với HLV " + appointment.getStaff().getFullName(),
                appointment.getStartTime().format(formatter)
        );

        sendEmailWithTemplate(
                appointment.getStaff().getEmail(),
                appointment.getStaff().getFullName(),
                "Lịch dạy mới",
                "Bạn có lịch dạy 1-1 với học viên.",
                "Học viên: " + appointment.getPtPackageIssued().getMember().getFullName(),
                appointment.getStartTime().format(formatter)
        );
    }

    @Override
    public void sendClassReminder(MemberRegistration registration) {
        validateRegistration(registration);

        String memberEmail = registration.getMember().getEmail();
        String memberName = registration.getMember().getFullName();
        String className = registration.getClassSchedule().getFitnessClass().getName();
        String startTime = registration.getClassSchedule().getStartTime().format(formatter);

        System.out.println(">>> Đang gửi mail cho: " + memberEmail + " - Lớp: " + className);

        sendEmailWithTemplate(
                memberEmail,
                memberName,
                "Nhắc nhở lịch học: " + className,
                "Đừng quên bạn có lịch tham gia lớp học nhóm hôm nay.",
                "Lớp: " + className,
                startTime
        );
    }

    private void sendEmailWithTemplate(String to, String name, String subject, String message, String activity, String time) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("message", message);
        context.setVariable("activity", activity);
        context.setVariable("time", time);

        String htmlBody = templateEngine.process("email-template", context);

        emailService.sendHtmlEmail(to, subject, htmlBody);
    }

    private void validateRegistration(MemberRegistration registration) {
        if (registration.getMember() == null || registration.getMember().getEmail() == null) {
            throw new RuntimeException("Hội viên không có email! ID: " + registration.getMember().getId());
        }
        if (registration.getClassSchedule() == null || registration.getClassSchedule().getFitnessClass() == null) {
            throw new RuntimeException("Lịch học không hợp lệ!");
        }
    }
}
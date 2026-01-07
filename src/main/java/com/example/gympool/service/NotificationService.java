package com.example.gympool.service;

import com.example.gympool.entity.MemberRegistration;
import com.example.gympool.entity.PTAppointment;

public interface NotificationService {
    void sendAppointmentReminder(PTAppointment appointment);
    void sendClassReminder(MemberRegistration memberRegistration);

}

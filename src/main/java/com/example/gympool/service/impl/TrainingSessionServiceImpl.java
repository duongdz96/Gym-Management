package com.example.gympool.service.impl;

import com.example.gympool.entity.PTAppointment;
import com.example.gympool.entity.PTPackageIssued;
import com.example.gympool.entity.TrainingSession;
import com.example.gympool.entity.TrainingSession;
import com.example.gympool.repository.PTAppointmentRepository;
import com.example.gympool.repository.PTPackageIssuedRepository;
import com.example.gympool.repository.TrainingSessionRepository;
import com.example.gympool.service.TrainingSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingSessionServiceImpl implements TrainingSessionService {
    private final TrainingSessionRepository trainingSessionRepository;
    private final PTAppointmentRepository ptAppointmentRepository;
    private final PTPackageIssuedRepository ptPackageIssuedRepository;
    @Override
    public List<TrainingSession> getAllTrainingSession(){
        return trainingSessionRepository.findAll();
    }
    @Override
    public TrainingSession getTrainingSessionById(Long id){
        return trainingSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TrainingSession not found with id: " + id));
    }
    @Override
    public TrainingSession addTrainingSession(TrainingSession trainingSession) {
        return trainingSessionRepository.save(trainingSession);
    }
    @Override
    public TrainingSession getTrainingSessionByCustomerName(String name){
        return trainingSessionRepository.findByMemberName(name)
                .orElseThrow(() -> new IllegalArgumentException("TrainingSession not found with MemberName: " + name));
    }
    @Override
    public TrainingSession getTrainingSessionByPtName(String name){
        return trainingSessionRepository.findByStaffName(name)
                .orElseThrow(() -> new IllegalArgumentException("TrainingSession not found with StaffName: " + name));
    }
    @Override
    @Transactional
    public TrainingSession startSession(Long appointmentId, String note){
        PTAppointment appointment = ptAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        PTPackageIssued ptPackageIssued = appointment.getPtPackageIssued();
        Integer remaining = ptPackageIssued.getRemainingSessions();
        if (remaining == null || remaining <= 0) {
            throw new RuntimeException("No remaining sessions in this PT package");
        }
        ptPackageIssued.setRemainingSessions(remaining - 1);
        ptPackageIssuedRepository.save(ptPackageIssued);
        TrainingSession session = new TrainingSession();
        session.setPtAppointment(appointment);
        session.setStartAt(LocalDateTime.now());
        session.setEndAt(null);
        session.setNote(note);
        return trainingSessionRepository.save(session);
    }
    @Override
    @Transactional
    public TrainingSession endSession(Long appointmentId, String note) {
        TrainingSession session = trainingSessionRepository.findByPtAppointmentId(appointmentId).
                orElseThrow(() -> new RuntimeException("Appointment not found"));
        session.setEndAt(LocalDateTime.now());
        return trainingSessionRepository.save(session);
    }
}

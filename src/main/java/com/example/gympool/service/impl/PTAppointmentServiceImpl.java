package com.example.gympool.service.impl;

import com.example.gympool.entity.PTAppointment;
import com.example.gympool.entity.PTAppointment;
import com.example.gympool.repository.PTAppointmentRepository;
import com.example.gympool.service.PTAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PTAppointmentServiceImpl implements PTAppointmentService {
    private final PTAppointmentRepository ptAppointmentRepository;
    @Override
    public List<PTAppointment> getAllPTAppointment(){
        return ptAppointmentRepository.findAll();
    }
    @Override
    public PTAppointment getPTAppointmentById(Long id){
        return ptAppointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PTAppointment not found with id: " + id));
    }
    @Override
    public PTAppointment addPTAppointment(PTAppointment ptAppointment) {
        return ptAppointmentRepository.save(ptAppointment);
    }

    @Override
    public PTAppointment updatePTAppointment(Long id, PTAppointment PTAppointmentUpd) {
        PTAppointment PTAppointment = getPTAppointmentById(id);

        if (PTAppointmentUpd.getStartTime() != null) PTAppointment.setStartTime(PTAppointmentUpd.getStartTime());
        if (PTAppointmentUpd.getEndTime() != null) PTAppointment.setEndTime(PTAppointmentUpd.getEndTime());
        if (PTAppointmentUpd.getStaff() != null) PTAppointment.setStaff(PTAppointmentUpd.getStaff());
        if (PTAppointmentUpd.getStatus() != null) PTAppointment.setStatus(PTAppointmentUpd.getStatus());
        return ptAppointmentRepository.save(PTAppointment);
    }
    @Override
    public PTAppointment cancelAppointment(Long id){
        PTAppointment PTAppointment = getPTAppointmentById(id);
        PTAppointment.setStatus("CANCELLED");
        return ptAppointmentRepository.save(PTAppointment);
    }
    @Override
    public PTAppointment getPTAppointmentByCustomerName(String name){
        return ptAppointmentRepository.findByMemberName(name)
                .orElseThrow(() -> new IllegalArgumentException("PTAppointment not found with MemberName: " + name));
    }
    @Override
    public PTAppointment getPTAppointmentByPTName(String name){
        return ptAppointmentRepository.findByStaffName(name)
                .orElseThrow(() -> new IllegalArgumentException("PTAppointment not found with StaffName: " + name));
    }
    @Override
    public List<PTAppointment> getUpcomingPTAppointment(Long staffId){
        return ptAppointmentRepository.findTop5ByStaffIdAndStartTimeAfterOrderByStartTimeAsc(staffId,LocalDateTime.now());
    }
    public List<PTAppointment> getUpcomingMemberAppointment(Long memberId){
        return ptAppointmentRepository.findTop5ByPtPackageIssuedMemberIdAndStartTimeAfterOrderByStartTimeAsc(memberId,LocalDateTime.now());
    }
}

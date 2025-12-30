package com.example.gympool.service.impl;

import com.example.gympool.entity.Staff;
import com.example.gympool.entity.Status;
import com.example.gympool.repository.StaffRepository;
import com.example.gympool.service.StaffService;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public Staff createStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    @Override
    public Optional<Staff> getStaffByEmail(String email) {
        return staffRepository.findByEmail(email);
    }

    @Override
    public Staff updateStaff(Long id, Staff staff) {
        Staff existing = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id " + id));
        existing.setPosition(staff.getPosition());
        existing.setSpecialize(staff.getSpecialize());
        existing.setHirePrice(staff.getHirePrice());
        return staffRepository.save(existing);
    }

    @Override
    public void deleteStaff(Long id) {
        Staff staff = getStaffById(id);
        staff.setStatus(String.valueOf(Status.INACTIVE));
        staffRepository.save(staff);
    }

    @Override
    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id " + id));
    }

    @Override
    public List<Staff> getAllStaffs() {
        return staffRepository.findAll();
    }
}

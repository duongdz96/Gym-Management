package com.example.gympool.service.impl;

import com.example.gympool.entity.*;
import com.example.gympool.entity.StudentProfile;
import com.example.gympool.repository.*;
import com.example.gympool.repository.StudentProfileRepository;
import com.example.gympool.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl implements StudentProfileService {
    private final StudentProfileRepository studentProfileRepository;
    private final MemberRepository memberRepository;
    private final PTPackageIssuedRepository ptPackageIssuedRepository;
    private final PTPackageRepository ptPackageRepository;
    @Override
    public List<StudentProfile> getAllStudentProfile(){
        List<StudentProfile> studentProfileList = studentProfileRepository.findAll();
        for (StudentProfile studentProfile : studentProfileList){
            studentProfile=getStudentProfileDetails(studentProfile);
        }
        return studentProfileList;
    }
    @Override
    public StudentProfile getStudentProfileById(Long id){
        StudentProfile studentProfile= studentProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StudentProfile not found with id: " + id));
        studentProfile=getStudentProfileDetails(studentProfile);
        return studentProfile;
    }
    @Override
    public StudentProfile addStudentProfile(StudentProfile ptPackage) {
        return studentProfileRepository.save(ptPackage);
    }

    @Override
    public StudentProfile updateStudentProfile(Long id, StudentProfile StudentProfileUpd) {
        StudentProfile studentProfile = getStudentProfileById(id);

        if (StudentProfileUpd.getHeight() != null) studentProfile.setHeight(StudentProfileUpd.getHeight());
        if (StudentProfileUpd.getWeight() != null) studentProfile.setWeight(StudentProfileUpd.getWeight());
        if (StudentProfileUpd.getStaff() != null) studentProfile.setStaff(StudentProfileUpd.getStaff());
        if (StudentProfileUpd.getTrainingPlan() != null) studentProfile.setTrainingPlan(StudentProfileUpd.getTrainingPlan());
        return studentProfileRepository.save(studentProfile);
    }
    private StudentProfile getStudentProfileDetails(StudentProfile studentProfile){
        Member member= memberRepository.findById(studentProfile.getMember().getId()).
                orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + studentProfile.getMember().getId()));
        PTPackageIssued ptPackageIssued=ptPackageIssuedRepository.findById(studentProfile.getPtPackageIssued().getId()).
                orElseThrow(() -> new IllegalArgumentException("ptPackageIssued not found with id: " + studentProfile.getPtPackageIssued().getId()));
        PTPackage ptPackage=ptPackageRepository.findById(ptPackageIssued.getPtPackage().getId()).
                orElseThrow(() -> new IllegalArgumentException("ptPackage not found with id: " + ptPackageIssued.getPtPackage().getId()));
        ptPackageIssued.setPtPackage(ptPackage);
        studentProfile.setPtPackageIssued(ptPackageIssued);
        studentProfile.setMember(member);
        return studentProfile;
    }
    @Override
    public StudentProfile getStudentProfileByCustomerName(String name){
        StudentProfile studentProfile= studentProfileRepository.findByMemberName(name)
                .orElseThrow(() -> new IllegalArgumentException("StudentProfile not found with MemberName: " + name));
        studentProfile=getStudentProfileDetails(studentProfile);
        return studentProfile;
    }
    @Override
    public StudentProfile getStudentProfileByStaffName(String name){
        StudentProfile studentProfile= studentProfileRepository.findByStaffName(name)
                .orElseThrow(() -> new IllegalArgumentException("StudentProfile not found with StaffName: " + name));
        studentProfile=getStudentProfileDetails(studentProfile);
        return studentProfile;
    }
}

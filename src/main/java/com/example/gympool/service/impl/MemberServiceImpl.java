package com.example.gympool.service.impl;

import com.example.gympool.entity.Member;
import com.example.gympool.repository.MemberRepository;
import com.example.gympool.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));
    }

    @Override
    public Member createMember(Member member) {
        if (member.getPassword() == null || member.getPassword().isEmpty()) {
            // TODO: Generate random password and send via email
            // String randomPassword = generateRandomPassword();
            // emailService.sendPassword(member.getEmail(), randomPassword);
            // member.setPassword(passwordEncoder.encode(randomPassword));
            
            // Temporary default password
            member.setPassword(passwordEncoder.encode("123456"));
        } else {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        }
        return memberRepository.save(member);
    }

    @Override
    public Member updateMember(Long id, Member memberDetails) {
        Member member = getMemberById(id);

        // update field từ User
        if (memberDetails.getEmail() != null) member.setEmail(memberDetails.getEmail());
        if (memberDetails.getPassword() != null) member.setPassword(memberDetails.getPassword());
        if (memberDetails.getFullName() != null) member.setFullName(memberDetails.getFullName());
        if (memberDetails.getDob() != null) member.setDob(memberDetails.getDob());
        if (memberDetails.getGender() != null) member.setGender(memberDetails.getGender());
        if (memberDetails.getPhone() != null) member.setPhone(memberDetails.getPhone());

        // update field riêng của Member
        if (memberDetails.getMembership() != null) member.setMembership(memberDetails.getMembership());
        if (memberDetails.getJoinDate() != null) member.setJoinDate(memberDetails.getJoinDate());
        if (memberDetails.getStatus() != null) member.setStatus(memberDetails.getStatus());
        if (memberDetails.getCardId() != null) member.setCardId(memberDetails.getCardId());
        if (memberDetails.getFaceId() != null) member.setFaceId(memberDetails.getFaceId());
        return memberRepository.save(member);
    }

    @Override
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
}

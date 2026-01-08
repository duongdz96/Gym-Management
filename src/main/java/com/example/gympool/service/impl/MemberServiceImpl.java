package com.example.gympool.service.impl;

import com.example.gympool.entity.Member;
import com.example.gympool.entity.Status;
import com.example.gympool.repository.MemberRepository;
import com.example.gympool.service.EmailService;
import com.example.gympool.service.MemberService;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789";

    private static final int PASSWORD_LENGTH = 10;

    private final MemberRepository memberRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public MemberServiceImpl(MemberRepository memberRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    public void sendPassword(String toEmail, String rawPassword) {
        String subject = "Thông tin tài khoản";

        String body = ""
                + "Xin chào,\n\n"
                + "Tài khoản của bạn đã được tạo / đặt lại thành công.\n\n"
                + "Thông tin đăng nhập:\n"
                + "- Email: " + toEmail + "\n"
                + "- Mật khẩu: " + rawPassword + "\n\n"
                + "Vui lòng đăng nhập và đổi mật khẩu ngay sau lần đăng nhập đầu tiên "
                + "để đảm bảo an toàn cho tài khoản.\n\n"
                + "Trân trọng.";

        emailService.sendEmail(toEmail, subject, body);
    }


    @Override
    public Member createMember(Member member) {
        String randomPassword = generateRandomPassword();
        System.out.println("chuan bi gui mail" + member.getEmail() + " " + randomPassword);
        sendPassword(member.getEmail(), randomPassword);

        System.out.println("Da gui mail");
        member.setPassword(passwordEncoder.encode(randomPassword));
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
        Member member = getMemberById(id);
        member.setStatus(String.valueOf(Status.INACTIVE));
        memberRepository.save(member);
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElse(null);
    }
}
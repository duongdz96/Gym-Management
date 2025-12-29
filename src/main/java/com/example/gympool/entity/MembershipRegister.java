package com.example.gympool.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MembershipRegister {
    private String email;
    private String password;
    private String fullName;
    private Date dob;
    private String gender;
    private String phone;
    private Long planId;
}
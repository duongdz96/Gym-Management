package com.example.gympool.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private Date dob;
    private String gender;
    private String phone;
}

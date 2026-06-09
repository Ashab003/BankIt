package com.project.BankIt_backend.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {

    private String fullName;

    private String phoneNumber;

    private String email;
}
package com.robosoft.internmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgetPassword
{
    private String emailId;
    private String password;
    private String otp;
    private String expireTime;
}

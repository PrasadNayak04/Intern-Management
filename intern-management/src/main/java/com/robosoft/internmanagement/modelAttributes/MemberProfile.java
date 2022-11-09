package com.robosoft.internmanagement.modelAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MemberProfile
{
    private String name;
    private String emailId;
    private String photoUrl;
    private Long mobileNumber;
    private String designation;
    private String position;
}

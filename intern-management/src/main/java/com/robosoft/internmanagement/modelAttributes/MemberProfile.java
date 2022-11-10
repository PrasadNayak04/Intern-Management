package com.robosoft.internmanagement.modelAttributes;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class MemberProfile {

    private String name;
    private String emailId;
    private long mobileNumber;
    private MultipartFile photo;
    private String designation;
    private String position;
    private String password;

}

package com.robosoft.internmanagement.modelAttributes;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MemberProfile {

    private String name;
    private String emailId;
    private long mobileNumber;
    private MultipartFile photo;
    private String designation;
    private String position;
    private String password;

}

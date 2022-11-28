package com.robosoft.internmanagement.modelAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MemberProfile{

    @NotBlank(message = "Message")
    private String name;

    @NotBlank
    @Pattern(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.(com|in|org)")
    private String emailId;

    @Min(value = 1000000000L)
    @Max(value = 9999999999L)
    private long mobileNumber;

    private MultipartFile photo;

    @NotBlank
    private String designation;

    @NotBlank
    private String position;

    @NotBlank
    private String password;

}

package com.robosoft.internmanagement.modelAttribute;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CandidateProfile {

    private String name;
    private Date dob;
    private long mobileNumber;
    private String emailId;
    private String jobLocation;
    private String gender;
    private String position;
    private int expYear;
    private int expMonth;
    private String candidateType;
    private String contactPerson;
    private String languagesKnown;
    private List<WorkHistory> workHistories;
    private List<Education> educations;
    private Address address;
    private String softwareWorked;
    private String skills;
    private String about;
    private List<Link> links;
    private int currentCTC;
    private int expectedCTC;
    private MultipartFile attachment;
    private MultipartFile photo;

}

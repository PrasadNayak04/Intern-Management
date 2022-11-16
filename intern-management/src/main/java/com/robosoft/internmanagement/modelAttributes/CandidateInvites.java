package com.robosoft.internmanagement.modelAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CandidateInvites
{

    private String candidateName;
    private String designation;
    private long mobileNumber;
    private String location;
    private String jobDetails;
    private String candidateEmail;

}

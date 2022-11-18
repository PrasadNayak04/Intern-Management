package com.robosoft.internmanagement.model;

import lombok.Data;

@Data
public class RejectedCv
{

    private int applicationId;
    private String name;
    private String imageUrl;
    private String designation;
    private String location;
    private long mobileNumber;

}

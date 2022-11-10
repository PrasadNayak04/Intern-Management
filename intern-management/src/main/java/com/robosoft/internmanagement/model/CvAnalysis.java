package com.robosoft.internmanagement.model;

import lombok.Data;

import java.util.Date;

@Data
public class CvAnalysis
{
    private String designation;
    private int applicants;
    private Date recievedDate;
    private String status;
    private String location;
}

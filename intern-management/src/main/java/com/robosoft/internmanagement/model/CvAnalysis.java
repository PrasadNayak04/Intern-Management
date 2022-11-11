package com.robosoft.internmanagement.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CvAnalysis
{
    private String designation;
    private int applicants;
    private Date receivedDate;
    private String status;
    private List<String> locations;
}

package com.robosoft.internmanagement.modelAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AssignBoard
{
    private int applicationId;
    private String recruiterEmail;
    private String organizerEmail;
    private Date assignDate;
    private String status;

}

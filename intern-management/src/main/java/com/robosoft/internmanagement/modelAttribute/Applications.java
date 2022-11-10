package com.robosoft.internmanagement.modelAttribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Applications {

    private int applicationId;
    private String emailId;
    private String designation;
    private String location;
    private Date date;

}

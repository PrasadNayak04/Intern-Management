package com.robosoft.internmanagement.modelAttribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Date;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Education {

    private String institution;
    private String grade;
    private Date fromDate;
    private Date toDate;
    private Date date;
    private String location;

}

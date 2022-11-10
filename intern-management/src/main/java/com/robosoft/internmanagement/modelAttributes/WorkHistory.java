package com.robosoft.internmanagement.modelAttribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Date;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class WorkHistory {

    private String company;
    private String position;
    private Date fromDate;
    private Date toDate;
    private Date date;
    private String location;

}

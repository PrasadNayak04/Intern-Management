package com.robosoft.internmanagement.model;

import lombok.Data;

import java.sql.Date;

@Data
public class AssignBoardPage
{

    private String name;
    private String designation;
    private String location;
    private Date assignDate;
    private String organizer;

}

package com.robosoft.internmanagement.modelAttribute;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class WorkHistory {

    private String company;
    private String position;
    private Date fromDate;
    private Date toDate;
    private String location;

}

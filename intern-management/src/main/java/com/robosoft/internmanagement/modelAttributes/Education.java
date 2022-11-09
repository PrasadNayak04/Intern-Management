package com.robosoft.internmanagement.modelAttribute;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Education {

    private String institution;
    private String grade;
    private Date fromDate;
    private Date toDate;
    private String location;

}

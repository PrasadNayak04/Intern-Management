package com.robosoft.internmanagement.modelAttributes;

import lombok.*;
import java.sql.Date;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Education {

    private String institution;
    private String grade;
    private Date fromDate;
    private Date toDate;
    private String location;

}

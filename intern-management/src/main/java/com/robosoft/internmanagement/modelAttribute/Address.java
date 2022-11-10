package com.robosoft.internmanagement.modelAttribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Address {
    
    private String content;
    private String state;
    private long pinCode;
    
}

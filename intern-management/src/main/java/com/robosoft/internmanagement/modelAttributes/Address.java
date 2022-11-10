package com.robosoft.internmanagement.modelAttributes;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Address {

    private String content;
    private String state;
    private long pinCode;
    
}

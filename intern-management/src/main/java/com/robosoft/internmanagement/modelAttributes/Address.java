package com.robosoft.internmanagement.modelAttributes;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {

    private String content;
    private String state;
    private long pinCode;
}

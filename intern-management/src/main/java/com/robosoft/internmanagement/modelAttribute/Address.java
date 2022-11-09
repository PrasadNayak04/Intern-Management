package com.robosoft.internmanagement.modelAttribute;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {

    private String content;
    private String state;
    private long pinCode;
}

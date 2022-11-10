package com.robosoft.internmanagement.modelAttributes;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Address {

    private String content;
    private String state;
    private long pinCode;
    
}

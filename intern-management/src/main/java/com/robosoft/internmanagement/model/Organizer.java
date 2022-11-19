package com.robosoft.internmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Organizer implements Comparable<Organizer>
{
    private String name;
    private String profile;
    private int interviews;

    @Override
    public int compareTo(Organizer o) {
        if(this.interviews>o.interviews)
            return -1;

        if(this.interviews<o.interviews)
            return 1;

        return 0;

    }
}

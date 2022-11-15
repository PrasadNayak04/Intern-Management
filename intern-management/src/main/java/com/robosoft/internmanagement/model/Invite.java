package com.robosoft.internmanagement.model;

import lombok.Data;

@Data
public class Invite
{
    private int today;
    private int yesterday;
    private int pastMonth;
    private int passYear;
    private int twoYearBack;

}

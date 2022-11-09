package com.robosoft.internmanagement.modelAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Notifications
{
    private int notificationId;
    private int eventId;
    private String emailId;
    private String message;
    private String type;
}

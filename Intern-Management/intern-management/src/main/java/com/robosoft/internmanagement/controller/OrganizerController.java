package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrganizerController
{
    @Autowired
    OrganizerService organizerService;

    @PutMapping("/take-interview")
    public String assignStatus(@ModelAttribute AssignBoard board)
    {
       return organizerService.takeInterview(board);
    }

}

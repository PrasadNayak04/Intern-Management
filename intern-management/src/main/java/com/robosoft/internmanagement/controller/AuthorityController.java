package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.modelAttributes.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthorityController
{
    @Autowired
    AuthorityService authorityService;

    @GetMapping("/all-applicants")
    public List<Applications> allApplicants()
    {
        return authorityService.getApplicants();
    }

    @PostMapping("/assign-recruiter")
    public String setRecruiter(@ModelAttribute AssignBoard assignBoard)
    {
        return authorityService.assignRecruiter(assignBoard);
    }
}

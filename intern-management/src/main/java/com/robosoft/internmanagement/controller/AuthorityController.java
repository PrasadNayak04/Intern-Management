package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.model.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/intern-management/authority")
public class AuthorityController
{
    @Autowired
    AuthorityService authorityService;

    @GetMapping("/available-recruiters")
    public ResponseEntity<?> getAllRecruiters(){
        return ResponseEntity.ok(authorityService.getAllRecruiters());
    }

    @GetMapping("/applicants")
    public List<Applications> allApplicants()
    {
        return authorityService.getApplicants();
    }

    @PostMapping("/recruiter-assignation")
    public String setRecruiter(@ModelAttribute AssignBoard assignBoard)
    {
        return authorityService.assignRecruiter(assignBoard);
    }
}


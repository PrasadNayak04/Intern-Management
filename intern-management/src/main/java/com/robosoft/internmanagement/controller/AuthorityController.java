package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.model.Application;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.modelAttributes.Technology;
import com.robosoft.internmanagement.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/intern-management/authority")
public class AuthorityController
{
    @Autowired
    AuthorityService authorityService;

    @PostMapping("/new-technology")
    public ResponseEntity<?> addNewTechnology(@RequestBody Technology technology, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body((authorityService.addTechnology(technology, request)));
    }
    @GetMapping("/available-recruiters")
    public ResponseEntity<?> getAllRecruiters(){
        return ResponseEntity.ok(authorityService.getAllRecruiters());
    }

    @GetMapping("/applicants")
    public List<Application> allApplicants()
    {
        return authorityService.getApplicants();
    }

    @PostMapping("/recruiter-assignation")
    public String setRecruiter(@ModelAttribute AssignBoard assignBoard)
    {
        return authorityService.assignRecruiter(assignBoard);
    }
}


package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.modelAttributes.CandidateProfile;
import com.robosoft.internmanagement.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/intern-management/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping("/register")
    public String candidateRegister(@ModelAttribute CandidateProfile candidateProfile, HttpServletRequest request) throws Exception {
        System.out.println(candidateProfile);
        return candidateService.candidateRegister(candidateProfile,request);
    }



}

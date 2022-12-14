package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.modelAttributes.CandidateProfile;
import com.robosoft.internmanagement.service.CandidateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping(value = "/intern-management/candidate")
public class CandidateController {

    @Autowired
    private CandidateServices candidateServices;

    @PostMapping("/register")
    public ResponseEntity<?> candidateRegister(@Valid @ModelAttribute CandidateProfile candidateProfile, HttpServletRequest request) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(candidateServices.candidateRegister(candidateProfile,request));
    }

}

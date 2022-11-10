package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.modelAttributes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CandidateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StorageService storageService;

    String query;

    public String candidateRegister(CandidateProfile candidateProfile, HttpServletRequest request){
        return "";
    }


}

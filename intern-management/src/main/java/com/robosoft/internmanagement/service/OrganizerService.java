package com.robosoft.internmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrganizerService
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    private String query;

    public String takeInterview(int applicationId){
        return "";
    }
}

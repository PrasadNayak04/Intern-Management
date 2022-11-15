package com.robosoft.internmanagement.service;


import com.robosoft.internmanagement.modelAttributes.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityService
{

    @Autowired
    JdbcTemplate jdbcTemplate;

    private String query;

    public List<Applications> getApplicants()
    {
        query = "select applicationId,emailId,designation,location,date from applications where applicationId NOT IN (select applicationId from assignBoard)";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Applications.class));
    }

    public String assignRecruiter(AssignBoard assignBoard)
    {
        try
        {
            query = "select name from memberProfile where emailId=? and position=?";
            jdbcTemplate.queryForObject(query, String.class,assignBoard.getRecruiterEmail(),"Recruiter");

            try {
                query = "insert into assignBoard(applicationId,recruiterEmail) values(?,?)";
                jdbcTemplate.update(query,assignBoard.getApplicationId(),assignBoard.getRecruiterEmail());
                return "Recruiter Assigned Successfully";
            }
            catch (Exception e)
            {
                return "Applicant is assigned already";
            }
        }
        catch (Exception e) {
            return "Select correct Recruiter to assign";
        }
    }
}

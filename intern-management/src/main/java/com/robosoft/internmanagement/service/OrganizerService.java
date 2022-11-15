package com.robosoft.internmanagement.service;


import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrganizerService
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    private String query;

    public String takeInterview(AssignBoard board){
        try{
            query = "select status from assignBoard where applicationId=? and organizerEmail=? and status=?";
            jdbcTemplate.queryForObject(query,String.class,board.getApplicationId(),board.getOrganizerEmail(),"new");

            query = "update assignBoard set status=? where applicationId=? and organizerEmail=? and status=?";
            jdbcTemplate.update(query,board.getStatus(),board.getApplicationId(),board.getOrganizerEmail(),"new");
        }
        catch (Exception e)
        {
            return "Invalid information";
        }
        return "Interview Completed Successfully";
    }
}

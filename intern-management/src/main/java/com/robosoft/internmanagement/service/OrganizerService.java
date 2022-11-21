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

    public String takeInterview(AssignBoard board){
        if(!(MemberService.getCurrentUser().equals(board.getOrganizerEmail()))){
            return "You can only take interviews which are assigned to you.";
        }
        try{
            String query = "select status from Assignboard where candidateId=? and organizerEmail=? and status=? and deleted = 0";
            jdbcTemplate.queryForObject(query,String.class,board.getCandidateId(),board.getOrganizerEmail(),"NEW");

            query = "update Assignboard set status=? where candidateId=? and organizerEmail=? and status=?";
            jdbcTemplate.update(query,board.getStatus(),board.getCandidateId(),board.getOrganizerEmail(),"NEW");
        }
        catch (Exception e)
        {
            return "Invalid information";
        }
        return "Interview Completed Successfully";
    }

}

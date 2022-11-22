package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.model.AssignBoardPage;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class OrganizerService implements OrganizerServices
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberService memberService;

    public String takeInterview(AssignBoard board, HttpServletRequest request){
        if(!(memberService.getUserNameFromRequest(request).equals(board.getOrganizerEmail()))){
            return "You can only take interviews which are assigned to you.";
        }
        try{
            String query = "select status from Assignboard where candidateId=? and organizerEmail=? and status=? and deleted = 0";
            jdbcTemplate.queryForObject(query,String.class,board.getCandidateId(),board.getOrganizerEmail(),"NEW");

            query = "update Assignboard set status=? where candidateId=? and organizerEmail=? and status=?";
            jdbcTemplate.update(query,board.getStatus(),board.getCandidateId(),board.getOrganizerEmail(),"NEW");

            if(board.getStatus().equalsIgnoreCase("SHORTLISTED")){
                query = "select designation, locations Applications where candidateId = ? and deleted = 0";
                AssignBoardPage assignBoard = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(AssignBoardPage.class), board.getCandidateId());

                query = "update Locations set vacancy = vacancy - 1 where designation = ? and location = ?";
                jdbcTemplate.update(query, assignBoard.getDesignation(), assignBoard.getLocation());

                query = "update Technologies set vacancy = vacancy - 1 where designation = ?";
                jdbcTemplate.update(query, assignBoard.getDesignation());

            }
        }
        catch (Exception e)
        {
            return "Invalid information";
        }
        return "Interview Completed Successfully";
    }

}

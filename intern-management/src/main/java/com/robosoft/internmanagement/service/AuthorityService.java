package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.model.Application;
import com.robosoft.internmanagement.model.MemberModel;
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

    public List<?> getAllRecruiters(){
        query = "select emailId, name, photoUrl from MembersProfile where position = 'RECRUITER'";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(MemberModel.class));
    }

    public List<Application> getApplicants()
    {
        query = "select candidateId, imageUrl, emailId, mobileNumber, designation,location,date from Applications inner join CandidatesProfile using(candidateId) inner join Documents using(candidateId) where candidateId NOT IN (select candidateId from Assignboard where Assignboard.deleted = 0) and Applications.deleted = 0 and Documents.deleted = 0 and CandidatesProfile.deleted = 0";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Application.class));
    }

    public String assignRecruiter(AssignBoard assignBoard)
    {
        try
        {
            query = "select name from MembersProfile where emailId=? and position=?";
            jdbcTemplate.queryForObject(query, String.class,assignBoard.getRecruiterEmail(),"RECRUITER");

            try {
                query = "insert into Assignboard(candidateId,recruiterEmail) values(?,?)";
                jdbcTemplate.update(query,assignBoard.getCandidateId(),assignBoard.getRecruiterEmail());
                return "Recruiter Assigned Successfully";
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                return "Applicant is assigned already";
            }
        }
        catch (Exception e) {
            return "Select correct Recruiter to assign";
        }
    }

}

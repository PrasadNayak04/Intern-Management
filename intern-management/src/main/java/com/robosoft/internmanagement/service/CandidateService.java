package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.modelAttribute.CandidateProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CandidateService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    StorageService storageService;
    public String candidateRegister(CandidateProfile candidateProfile, HttpServletRequest request) throws Exception {
        try {
            String query1 = "insert into candidateprofile(name,dob,mobileNumber,emailId,jobLocation,gender,position,expYear,expMonth,candidateType,contactPerson,languagesKnown,softwaresWorked,skills,about,currentCTC,expectedCTC) values('" + candidateProfile.getName() + "','" + candidateProfile.getDob() + "','" + candidateProfile.getMobileNumber() + "','" + candidateProfile.getEmailId() + "','" + candidateProfile.getJobLocation() + "','" + candidateProfile.getGender() + "','" + candidateProfile.getPosition() + "','" + candidateProfile.getExpYear() + "','" + candidateProfile.getExpMonth() + "','" + candidateProfile.getCandidateType() + "','" + candidateProfile.getContactPerson() + "','" + candidateProfile.getLanguagesKnown() + "','" + candidateProfile.getSoftwareWorked() + "','" + candidateProfile.getSkills() + "','" + candidateProfile.getAbout() + "','" + candidateProfile.getCurrentCTC() + "','" + candidateProfile.getExpectedCTC() + "'";
            jdbcTemplate.update(query1);
            Boolean photoRes = storageService.singleFileUpload(candidateProfile.getPhoto(), candidateProfile.getEmailId(), request);
            Boolean resumeRes = storageService.singleFileUpload(candidateProfile.getAttachment(), candidateProfile.getEmailId(), request);
        } catch (Exception e) {
            delCandidateQuery(candidateProfile.getEmailId());
            //delete local photo query needed
        }





    }
    public void delCandidateQuery(String emailId) {
        String delQuery = "delete from candidateprofile where emailId = '"+ emailId+"'";
        jdbcTemplate.update(delQuery);
    }
}

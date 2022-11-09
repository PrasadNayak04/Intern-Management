package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.modelAttribute.CandidateProfile;
import com.robosoft.internmanagement.modelAttribute.MemberProfile;
import com.robosoft.internmanagement.service.JwtSecurity.BeanStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CandidateService {

    @Autowired
    private BeanStore beanStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StorageService storageService;

    String query;

    public String candidateRegister(CandidateProfile candidateProfile, HttpServletRequest request){
        return "";
    }

    public String registerMember(MemberProfile memberProfile, HttpServletRequest request){
        memberProfile.setPassword(encodePassword(memberProfile.getPassword()));
        try{
            query = "insert into member values(?,?)";
            jdbcTemplate.update(query, memberProfile.getEmailId(), memberProfile.getPassword());
            query = "insert into memberProfile(name, emailId, mobileNumber, designation, position) values (?,?,?,?,?)";
            jdbcTemplate.update(query, memberProfile.getName(), memberProfile.getEmailId(), memberProfile.getMobileNumber(), memberProfile.getDesignation(), memberProfile.getPosition());
            String photoDownloadUrl = storageService.singleFileUpload(memberProfile.getPhoto(), memberProfile.getEmailId(), request);
            query = "update memberProfile set photoUrl = ? where emailId = ?";
            jdbcTemplate.update(query, photoDownloadUrl, memberProfile.getEmailId());
            return "User credentials saved";
        } catch(Exception e){
            query = "delete from member where emailId = ?";
            jdbcTemplate.update(query, memberProfile.getEmailId());
            return "Unable to save user credentials";
        }
    }

    public String encodePassword(String password){
        return beanStore.passwordEncoder().encode(password);
    }

}

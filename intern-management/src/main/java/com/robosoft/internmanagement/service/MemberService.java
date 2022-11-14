package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.modelAttributes.Member;
import com.robosoft.internmanagement.modelAttributes.MemberProfile;
import com.robosoft.internmanagement.service.JwtSecurity.BeanStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MemberService {

    @Autowired
    private BeanStore beanStore;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StorageService storageService;

    private static String currentUser;

    public static void setCurrentUser(String currentUser) {
        MemberService.currentUser = currentUser;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    String query;


    public Member getMemberByEmail(String memberEmail){
        try {
            System.out.println("inside member email" + memberEmail);
            query = "select password from member where emailId = '" + memberEmail + "'";
            String bcryptPass = jdbcTemplate.queryForObject(query, String.class);
            return new Member(memberEmail, bcryptPass);

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String registerMember(MemberProfile memberProfile, HttpServletRequest request){
        memberProfile.setPassword(encodePassword(memberProfile.getPassword()));
        try{
            query = "insert into member(emailId, password) values(?,?)";
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
            e.printStackTrace();
            return "Unable to save user credentials";
        }
    }

    public int updatePassword(Member member){
        query = "update member set password = ? where emailId = ?";
        try{
            return jdbcTemplate.update(query, encodePassword(member.getPassword()), member.getEmailId());
        }catch (Exception e){
            return 0;
        }
    }

    public String encodePassword(String password){
        return beanStore.passwordEncoder().encode(password);
    }

}

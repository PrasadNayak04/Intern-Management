package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.modelAttributes.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    String query = "select emailId, password from member where emailId = ?";

    public Member getMemberByEmail(String memberEmail){
        try{
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Member.class), memberEmail);
        }catch (UsernameNotFoundException usernameNotFoundException){
            return null;
        }
    }

}

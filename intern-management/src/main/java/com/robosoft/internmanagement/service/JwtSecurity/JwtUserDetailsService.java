package com.robosoft.internmanagement.service.JwtSecurity;


import com.robosoft.internmanagement.modelAttributes.Member;
import com.robosoft.internmanagement.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {
        try {
            Member member = Optional.of(memberService.getMemberByEmail(memberEmail)).orElse(null);
            return new org.springframework.security.core.userdetails.User(member.getEmailId(), member.getPassword(),
                    new ArrayList<>());
        }
        catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException("User not found with username: " + memberEmail);
        }
    }

    public String greet(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "Hello " + userDetails.getUsername();
    }

}

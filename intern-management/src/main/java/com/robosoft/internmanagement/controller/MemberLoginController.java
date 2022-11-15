package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.modelAttributes.Member;
import com.robosoft.internmanagement.modelAttributes.MemberProfile;
import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.JwtSecurity.JwtUserDetailsService;
import com.robosoft.internmanagement.service.JwtSecurity.TokenManager;
import com.robosoft.internmanagement.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/intern-management/member-login")
public class MemberLoginController {


    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private EmailService emailService;


    @PostMapping("/register")
    public String registerMember(@ModelAttribute MemberProfile memberProfile, HttpServletRequest request){
        return memberService.registerMember(memberProfile, request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@ModelAttribute Member member, HttpServletRequest request) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getEmailId(), member.getPassword()));
        } catch (DisabledException e) {
            e.printStackTrace();
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmailId());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        MemberService.setCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/otp")
    public ResponseEntity<?> sendMail(@RequestParam String toEmail){
        boolean mailSent = emailService.sendEmail(toEmail);

        if(mailSent){
            return ResponseEntity.ok().body("Otp has been sent to the email \"" + toEmail + "\"");
        }else{
            return ResponseEntity.status(HttpStatus.valueOf("Please provide valid email.")).build();
        }
    }

    @PutMapping("/otp-verification")
    public String verify(@RequestParam String emailId,@RequestParam String otp)
    {
        return emailService.verification(emailId,otp);
    }

    @PatchMapping("/password-update")
    public ResponseEntity<?> updatePassword(@ModelAttribute Member member){
        int updateStatus = memberService.updatePassword(member);
        if(updateStatus == 1){
            return ResponseEntity.ok("Password updated successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password");
    }

}

package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.constants.AppConstants;
import com.robosoft.internmanagement.exception.JwtTokenException;
import com.robosoft.internmanagement.exception.ResponseData;
import com.robosoft.internmanagement.modelAttributes.Member;
import com.robosoft.internmanagement.modelAttributes.MemberProfile;
import com.robosoft.internmanagement.service.EmailServices;
import com.robosoft.internmanagement.service.MemberServices;
import com.robosoft.internmanagement.service.jwtSecurity.JwtUserDetailsService;
import com.robosoft.internmanagement.service.jwtSecurity.TokenManager;
import io.jsonwebtoken.MalformedJwtException;
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
import javax.validation.Valid;
import java.awt.datatransfer.MimeTypeParseException;

@RestController
@CrossOrigin
@RequestMapping(value = "/intern-management/member-credentials")
public class MemberCredentialsController {

    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private MemberServices memberServices;

    @Autowired
    private EmailServices emailServices;


    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@Valid @ModelAttribute MemberProfile memberProfile, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(memberServices.registerMember(memberProfile, request));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> createToken(@RequestBody Member member, HttpServletRequest request) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getEmailId(), member.getPassword()));
            final UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmailId());
            final String jwtToken = tokenManager.generateJwtToken(userDetails);
            return ResponseEntity.ok(new ResponseData<>(jwtToken, AppConstants.SUCCESS));
        } catch (DisabledException e) {
            e.printStackTrace();
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new ResponseData<>("LOGIN_FAILED", AppConstants.INVALID_INFORMATION));
        }catch (JwtTokenException jwtTokenException){
            System.out.println("hi");
            return ResponseEntity.badRequest().body(new ResponseData<>(jwtTokenException.getMessage(), AppConstants.INVALID_INFORMATION));
        }
    }

    @PostMapping("/otp")
    public ResponseEntity<?> sendMail(@RequestParam String toEmail){
        boolean mailSent = emailServices.sendEmail(toEmail);
        if(mailSent){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("OTP SENT TO " + toEmail, AppConstants.SUCCESS));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ResponseData<>("FAILED", AppConstants.TASK_FAILED));
        }
    }

    @PutMapping("/otp-verification")
    public ResponseEntity<?> verify(@RequestParam String emailId,@RequestParam String otp)
    {
        String verificationStatus = emailServices.verification(emailId,otp);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(verificationStatus, AppConstants.SUCCESS));
    }

    @PatchMapping("/password-update")
    public ResponseEntity<?> updatePassword(@ModelAttribute Member member){
        int updateStatus = memberServices.updatePassword(member);
        if(updateStatus == 1)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("TASK SUCCESSFUL", AppConstants.SUCCESS));
        else if (updateStatus == -1)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("CHOOSE DIFFERENT PASSWORD", AppConstants.TASK_FAILED));
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("TASK FAILED", AppConstants.TASK_FAILED));
    }

}

package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.constants.AppConstants;
import com.robosoft.internmanagement.exception.JwtTokenException;
import com.robosoft.internmanagement.exception.ResponseData;
import com.robosoft.internmanagement.model.MemberModel;
import com.robosoft.internmanagement.modelAttributes.Member;
import com.robosoft.internmanagement.modelAttributes.MemberCredentials;
import com.robosoft.internmanagement.modelAttributes.MemberProfile;
import com.robosoft.internmanagement.service.EmailServices;
import com.robosoft.internmanagement.service.MemberServices;
import com.robosoft.internmanagement.service.jwtSecurity.JwtUserDetailsService;
import com.robosoft.internmanagement.service.jwtSecurity.TokenManager;
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

    @PostMapping(value = "/email-verification")
    public ResponseEntity<?> verifyMail(@RequestBody MemberCredentials memberCredentials){
        boolean mailSent = emailServices.sendRegistrationOtp(memberCredentials.getEmailId());
        if(mailSent){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(new MemberCredentials(memberCredentials.getName(), memberCredentials.getEmailId()), AppConstants.SUCCESS));
        }else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID INFORMATION", AppConstants.RECORD_ALREADY_EXIST));
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerMember(@Valid @ModelAttribute MemberProfile memberProfile, HttpServletRequest request){
        ResponseData<?> responseData = memberServices.registerMember(memberProfile, request);
        if(responseData.getResult().getOpinion().equals("T"))
            return ResponseEntity.status(HttpStatus.OK).body(responseData);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseData);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> createToken(@RequestBody Member member, HttpServletRequest request) throws Exception {
        try {
            MemberModel memberModel = memberServices.createLoggedInMemberModel(member.getEmailId());
            if(memberModel.getPosition().equalsIgnoreCase(member.getRole())) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getEmailId(), member.getPassword()));
                final UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmailId());
                final String jwtToken = tokenManager.generateJwtToken(userDetails);
                memberModel.setToken(jwtToken);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(memberModel, AppConstants.SUCCESS));
            }
        }
        catch (DisabledException e) {
            e.printStackTrace();
            throw new Exception("USER_DISABLED", e);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new ResponseData<>("LOGIN_FAILED", AppConstants.INVALID_INFORMATION));
        }
        catch (JwtTokenException jwtTokenException){
            return ResponseEntity.badRequest().body(new ResponseData<>(jwtTokenException.getMessage(), AppConstants.INVALID_INFORMATION));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("LOGIN FAILED", AppConstants.TASK_FAILED));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("LOGIN FAILED", AppConstants.TASK_FAILED));
    }

    @PostMapping("/otp")
    public ResponseEntity<?> sendMail(@RequestBody MemberCredentials memberCredentials){
        boolean mailSent = emailServices.sendEmail(memberCredentials.getEmailId());
        if(mailSent){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("SUCCESSFUL", AppConstants.SUCCESS));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ResponseData<>("FAILED", AppConstants.TASK_FAILED));
        }
    }

    @PutMapping("/otp-verification")
    public ResponseEntity<?> verify(@RequestBody MemberCredentials memberCredentials)
    {
        String response = emailServices.verification(memberCredentials.getEmailId(), memberCredentials.getOtp());
        if (response.equals("VERIFIED"))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(new MemberCredentials(memberCredentials.getName(), memberCredentials.getEmailId()), AppConstants.SUCCESS));

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>(response, AppConstants.TASK_FAILED));
    }

    @PatchMapping("/password-update")
    public ResponseEntity<?> updatePassword(@RequestBody Member member){
        int updateStatus = memberServices.updatePassword(member);
        if(updateStatus == 1)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("TASK SUCCESSFUL", AppConstants.SUCCESS));
        else if (updateStatus == -1)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("CHOOSE DIFFERENT PASSWORD", AppConstants.TASK_FAILED));
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("TASK FAILED", AppConstants.TASK_FAILED));
    }

}

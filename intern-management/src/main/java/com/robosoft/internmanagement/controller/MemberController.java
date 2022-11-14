package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.modelAttributes.*;
import com.robosoft.internmanagement.service.JwtSecurity.JwtUserDetailsService;
import com.robosoft.internmanagement.service.JwtSecurity.TokenManager;
import com.robosoft.internmanagement.service.MemberService;
import com.robosoft.internmanagement.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin
@RequestMapping("/intern-management/member")
public class MemberController {

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenManager tokenManager;

    @PostMapping("/register")
    public String registerMember(@ModelAttribute MemberProfile memberProfile, HttpServletRequest request){
        return memberService.registerMember(memberProfile, request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@ModelAttribute Member member, HttpServletRequest request) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getEmailId(), member.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmailId());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        MemberService.setCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(jwtToken); //new JwtResponseModel(jwtToken)
    }

    @PatchMapping("/password-update")
    public ResponseEntity<?> updatePassword(@ModelAttribute Member member){
        int updateStatus = memberService.updatePassword(member);
        if(updateStatus == 1){
            return ResponseEntity.ok("Password updated successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password");
    }

    @GetMapping("/fetch/{folderName}/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String folderName, @PathVariable String fileName, HttpServletRequest request) throws IOException {
        final String filePath = "src\\main\\resources\\static\\documents\\" + folderName + "\\" + fileName;
        Path path = Paths.get(filePath);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        // Try to determine file's content type
        String contentType = storageService.getContentType(request, resource);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


}

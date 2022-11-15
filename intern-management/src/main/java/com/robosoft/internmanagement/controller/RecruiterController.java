package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.modelAttributes.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.modelAttributes.CandidateInvites;
import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/intern-management/recruiter")
public class RecruiterController
{
    @Autowired
    EmailService emailService;

    @Autowired
    RecruiterService recruiterService;

    @GetMapping("/candidate-invitation")
    public ResponseEntity<String> invites(@ModelAttribute CandidateInvites invites)
    {
        boolean result = emailService.sendInviteEmail(invites);

        if (result){
            return ResponseEntity.ok().body("Invite sent to " + invites.getCandidateName());
        }else {
            return ResponseEntity.status(HttpStatus.valueOf("Insufficient information")).build();
        }
    }

    @GetMapping("/organizers")
    public List<Organizer> getList(@RequestParam String emailId)
    {
        return recruiterService.getOrganizer(emailId);
    }

    @GetMapping("/summary")
    public Summary getSummary()
    {
        return recruiterService.getSummary();
    }

    @GetMapping("/cv-count")
    public int getCv()
    {
        return recruiterService.cvCount();
    }

    @GetMapping("/logged-profile")
    public LoggedProfile getProfile()
    {
        return recruiterService.getProfile();
    }

    @GetMapping("/notification-display")
    public NotificationDisplay getNotifications()
    {
        return recruiterService.notification();
    }

    @GetMapping("/cv-analysis")
    public List<CvAnalysis> getCv (@RequestParam(required = false) Date date)
    {
        return recruiterService.cvAnalysisPage(date);
    }

    @GetMapping("/search/{designation}")
    public CvAnalysis search(@PathVariable String designation)
    {
        return recruiterService.searchDesignation(designation);
    }

    @PostMapping("/update-position-status")
    public int updatePositionStatus(@RequestParam String designation, @RequestParam String newStatus){
        return recruiterService.updateStatus(designation, newStatus);
    }
    @GetMapping("/top-technologies/{designation}")
    public ResponseEntity<?> getTopTechnologies(@PathVariable String designation) {
        List<TopTechnologies> technologies = recruiterService.getTopTechnologies(designation);
        if(technologies.get(0).getLocation().size()==0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Result not found for "+designation);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(technologies);
    }

    @GetMapping("/extended-cv/{emailId}")
    public ResponseEntity<?> getExtendedCV(@PathVariable String emailId){
        ExtendedCV extendedCV = recruiterService.getBasicCVDetails(emailId);
        if(extendedCV == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidate found with email " + emailId);
        }
        extendedCV.setEducations(recruiterService.getEducationsHistory(emailId));
        extendedCV.setWorkHistories(recruiterService.getWorkHistory(emailId));
        extendedCV.setLinks(recruiterService.getSocialLinks(emailId));
        return ResponseEntity.ok(extendedCV);
    }

    @GetMapping("/resume-url/{emailId}")
    public ResponseEntity<?> getResumeDownloadUrl(@PathVariable String emailId){
        String url = recruiterService.downloadCV(emailId);
        if(url.equals(null))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(url);
    }

    //pagination
    @GetMapping("/profiles/{designation}/{status}")
    public ResponseEntity<?> getProfileBasedOnStatus(@PathVariable String designation, @PathVariable String status) {
        return ResponseEntity.status(HttpStatus.FOUND).body(recruiterService.getProfileBasedOnStatus(designation, status));
    }

    @GetMapping("/applicants")
    public List<Applications> getApplicants()
    {
        return recruiterService.getNotAssignedApplicants();
    }

    @PutMapping("/organizer-assignation")
    public ResponseEntity<String> setOrganizer(@ModelAttribute AssignBoard assignBoard)
    {
        String result = recruiterService.assignOrganizer(assignBoard);
        if(result==null)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }else
            return ResponseEntity.of(Optional.of(result));
    }

    @GetMapping("/assignboard")
    public List<AssignBoardPage> getPage()
    {
        return recruiterService.getAssignBoardPage();
    }

    @GetMapping("/rejected-cv")
    public List<RejectedCv> getCvPage()
    {
        return recruiterService.getRejectedCvPage();
    }

}

package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.modelAttributes.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.modelAttributes.CandidateInvites;
import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.MemberService;
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
    private RecruiterService recruiterService;

    @Autowired
    private MemberService memberService;

    @PostMapping("/candidate-invitation")
    public ResponseEntity<String> invites(@ModelAttribute CandidateInvites invites)
    {
        boolean result = emailService.sendInviteEmail(invites);

        if (result){
            return ResponseEntity.ok().body("Invite sent to " + invites.getCandidateName());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/organizers")
    public ResponseEntity<?> getOrganizersList(@RequestParam (required = false) Integer limit)
    {
        if(!memberService.validPageDetails(1, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid limit count");
        }
        return ResponseEntity.ok(recruiterService.getOrganizer(limit));
    }

    @GetMapping("/summary")
    public Summary getSummary(@RequestParam Date date)
    {
        return recruiterService.getSummary(date);
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
    public ResponseEntity<?> getCv (@RequestParam(required = false) Date date, @RequestParam int pageNo, int limit)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.cvAnalysisPage(date, pageNo, limit));
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

    @GetMapping("/extended-cv/{applicationId}")
    public ResponseEntity<?> getExtendedCV(@PathVariable int applicationId){
        ExtendedCV extendedCV = recruiterService.getBasicCVDetails(applicationId);
        if(extendedCV == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidate found with application Id " + applicationId);
        }
        extendedCV.setEducations(recruiterService.getEducationsHistory(applicationId));
        extendedCV.setWorkHistories(recruiterService.getWorkHistory(applicationId));
        extendedCV.setLinks(recruiterService.getSocialLinks(applicationId));
        return ResponseEntity.ok(extendedCV);
    }

    @GetMapping("/resume-url/{applicationId}")
    public ResponseEntity<?> getResumeDownloadUrl(@PathVariable int applicationId){
        String url = recruiterService.downloadCV(applicationId);
        if(url.equals(null))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(url);
    }

    //pagination
    @GetMapping("/profiles/{designation}/{status}")
    public ResponseEntity<?> getProfileBasedOnStatus(@PathVariable String designation, @PathVariable String status, @RequestParam int pageNo, @RequestParam int limit) {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(recruiterService.getProfileBasedOnStatus(designation, status, pageNo, limit));
    }

    @GetMapping("/applicants")
    public List<Applications> getApplicants()
    {
        return recruiterService.getNotAssignedApplicants();
    }

    @PutMapping("/organizer-assignation")
    public ResponseEntity<?> setOrganizer(@ModelAttribute AssignBoard assignBoard)
    {
        String result = recruiterService.assignOrganizer(assignBoard);
        if(result==null)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }else
            return ResponseEntity.of(Optional.of(result));
    }

    @GetMapping("/assignboard")
    public ResponseEntity<?> getPage(@RequestParam int pageNo, @RequestParam int limit)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.getAssignBoardPage(pageNo, limit));
    }

    @GetMapping("/rejected-cv")
    public ResponseEntity<?> getCvPage(@RequestParam int pageNo, @RequestParam int limit)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }

        List<?> rejectedCvs = recruiterService.getRejectedCvPage(pageNo, limit);
        if(rejectedCvs.size() > 0){
            return ResponseEntity.status(HttpStatus.FOUND).body(rejectedCvs);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/invites-info")
    public ResponseEntity<Invite> getInfo()
    {
        if(recruiterService.getInviteInfo() == null){
            ResponseEntity.status(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(recruiterService.getInviteInfo());
    }

    @GetMapping("/invites-by-day")
    public ResponseEntity<?> getByDay(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.getByDay(date, pageNo, limit));
    }

    @GetMapping("/invites-by-month")
    public ResponseEntity<?> getByMonth(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.getByMonth(date, pageNo, limit));
    }

    @GetMapping("/invites-by-year")
    public ResponseEntity<?> getByYear(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.getByYear(date, pageNo, limit));
    }

    @PutMapping("/resend-invite")
    public String resendInvite(@RequestParam int inviteId)
    {
        boolean result = emailService.resendInvite(inviteId);
        if (result)
        {
            return "Invite sent";
        }
        return "Failed";
    }

}

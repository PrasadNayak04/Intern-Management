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
    public List<Organizer> getOrganizersList()
    {
        return recruiterService.getOrganizer();
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
    public List<?> getCv (@RequestParam(required = false) Date date, @RequestParam int pageNo, int limit)
    {
        return recruiterService.cvAnalysisPage(date, pageNo, limit);
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
    public List<AssignBoardPage> getPage()
    {
        return recruiterService.getAssignBoardPage();
    }

    @GetMapping("/rejected-cv")
    public ResponseEntity<?> getCvPage()
    {
        List<RejectedCv> rejectedCvs = recruiterService.getRejectedCvPage();
        if(rejectedCvs.size() > 0){
            return ResponseEntity.status(HttpStatus.FOUND).body(rejectedCvs);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/invite-info")
    public Invite getInfo()
    {
        return recruiterService.getInviteInfo();
    }

    @GetMapping("/invite-by-day")
    public List<SentInvites> getByDay(@RequestParam Date date)
    {
        return recruiterService.getByDay(date);
    }

    @GetMapping("/invite-by-month")
    public List<SentInvites> getByMonth(@RequestParam Date date)
    {
        return recruiterService.getByMonth(date);
    }

    @GetMapping("/invite-by-year")
    public List<SentInvites> getByYear(@RequestParam Date date)
    {
        return recruiterService.getByYear(date);
    }

    @PutMapping("/resend-invite")
    public String reSentInvite(@RequestParam int inviteId)
    {
        boolean result = emailService.reSentInvite(inviteId);
        if (result)
        {
            return "Invite sent";
        }
        return "Failed";
    }

}

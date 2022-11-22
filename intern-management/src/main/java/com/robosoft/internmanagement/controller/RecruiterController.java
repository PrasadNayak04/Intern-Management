package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.model.Application;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.modelAttributes.CandidateInvite;
import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.MemberService;
import com.robosoft.internmanagement.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<String> invites(@ModelAttribute CandidateInvite invites, HttpServletRequest request)
    {
        boolean result = emailService.sendInviteEmail(invites, request);

        if (result){
            return ResponseEntity.ok().body("Invite sent to " + invites.getCandidateName());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/available-organizers")
    public ResponseEntity<?> getAllOrganizers(){
        return ResponseEntity.ok(recruiterService.getAllOrganizers());
    }

    @GetMapping("/organizers")
    public ResponseEntity<?> getOrganizersList(@RequestParam (required = false) Integer limit, HttpServletRequest request)
    {
        if(!memberService.validPageDetails(1, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid limit count");
        }
        return ResponseEntity.ok(recruiterService.getOrganizer(limit, request));
    }

    @GetMapping("/summary")
    public Summary getSummary(@RequestParam Date date, HttpServletRequest request)
    {
        return recruiterService.getSummary(date, request);
    }

    @GetMapping("/cv-count")
    public int getCv(HttpServletRequest request)
    {
        return recruiterService.cvCount(request);
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
        List<TopTechnology> technologies = recruiterService.getTopTechnologies(designation);
        if(technologies.get(0).getLocation().size()==0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Result not found for "+designation);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(technologies);
    }

    @GetMapping("/extended-cv/{candidateId}")
    public ResponseEntity<?> getExtendedCV(@PathVariable int candidateId,HttpServletRequest request){
        ExtendedCV extendedCV = recruiterService.getBasicCVDetails(candidateId, request);
        if(extendedCV == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidate found with application Id " + candidateId);
        }
        extendedCV.setEducations(recruiterService.getEducationsHistory(candidateId));
        extendedCV.setWorkHistories(recruiterService.getWorkHistory(candidateId));
        extendedCV.setLinks(recruiterService.getSocialLinks(candidateId));
        return ResponseEntity.ok(extendedCV);
    }

    @GetMapping("/resume-url/{candidateId}")
    public ResponseEntity<?> getResumeDownloadUrl(@PathVariable int candidateId, HttpServletRequest request){
        String url = recruiterService.downloadCV(candidateId, request);
        if(url.equals(""))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(url);
    }

    //pagination
    @GetMapping("/profiles/{designation}/{status}")
    public ResponseEntity<?> getProfileBasedOnStatus(@PathVariable String designation, @PathVariable String status, @RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request) {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(recruiterService.getProfileBasedOnStatus(designation, status, pageNo, limit, request));
    }

    @GetMapping("/applicants")
    public List<Application> getApplicants(HttpServletRequest request)
    {
        return recruiterService.getNotAssignedApplicants(request);
    }

    @PutMapping("/organizer-assignation")
    public ResponseEntity<?> setOrganizer(@ModelAttribute AssignBoard assignBoard, HttpServletRequest request)
    {
        String result = recruiterService.assignOrganizer(assignBoard, request);
        if(result==null)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }else
            return ResponseEntity.of(Optional.of(result));
    }

    @GetMapping("/assignboard")
    public ResponseEntity<?> getPage(@RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.getAssignBoardPage(pageNo, limit, request));
    }

    @GetMapping("/rejected-cv")
    public ResponseEntity<?> getCvPage(@RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }

        List<?> rejectedCvs = recruiterService.getRejectedCvPage(pageNo, limit, request);
        if(rejectedCvs.size() > 0){
            return ResponseEntity.status(HttpStatus.FOUND).body(rejectedCvs);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/invites-info")
    public ResponseEntity<Invite> getInfo(HttpServletRequest request)
    {
        Invite invite = recruiterService.getInviteInfo(request);

        if(invite == null){
            ResponseEntity.status(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(invite);
    }

    @GetMapping("/invites-by-day")
    public ResponseEntity<?> getByDay(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.getByDay(date, pageNo, limit, request));
    }

    @GetMapping("/invites-by-month")
    public ResponseEntity<?> getByMonth(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.getByMonth(date, pageNo, limit, request));
    }

    @GetMapping("/invites-by-year")
    public ResponseEntity<?> getByYear(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberService.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid page details");
        }
        return ResponseEntity.ok(recruiterService.getByYear(date, pageNo, limit, request));
    }

    @PutMapping("/resend-invite")
    public String resendInvite(@RequestParam int inviteId, HttpServletRequest request)
    {
        boolean result = emailService.resendInvite(inviteId, request);
        if (result)
        {
            return "Invite sent";
        }
        return "Failed";
    }

}

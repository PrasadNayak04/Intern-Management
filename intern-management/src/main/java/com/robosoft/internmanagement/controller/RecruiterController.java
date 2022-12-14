package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.constants.AppConstants;
import com.robosoft.internmanagement.exception.ResponseData;
import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.model.Application;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.modelAttributes.CandidateInvite;
import com.robosoft.internmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/intern-management/recruiter")
public class RecruiterController
{
    @Autowired
    EmailServices emailServices;

    @Autowired
    private RecruiterServices recruiterServices;

    @Autowired
    private MemberServices memberServices;

    @PatchMapping("/candidate-rejection/{candidateId}")
    public ResponseEntity<?> rejectCandidate(@PathVariable int candidateId, HttpServletRequest request){
        if(recruiterServices.rejectAssignedCandidate(candidateId,request))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("TASK SUCCESSFUL", AppConstants.SUCCESS));

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("TASK FAILED", AppConstants.TASK_FAILED));
    }

    @PatchMapping("/candidate-recruitment/{candidateId}")
    public ResponseEntity<?> reRecruitCandidate(@PathVariable int candidateId, HttpServletRequest request){
        if(recruiterServices.reRecruitCandidate(candidateId,request))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("TASK SUCCESSFUL", AppConstants.SUCCESS));

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("TASK FAILED", AppConstants.TASK_FAILED));
    }

    @PatchMapping("/candidate-deletion/{candidateId}")
    public ResponseEntity<?> deleteCandidate(@PathVariable int candidateId, HttpServletRequest request){
        if(recruiterServices.deleteCandidate(candidateId,request))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("TASK SUCCESSFUL", AppConstants.SUCCESS));

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("TASK FAILED", AppConstants.TASK_FAILED));
    }

    @PostMapping("/candidate-invitation")
    public ResponseEntity<?> invite(@RequestBody CandidateInvite invitation, HttpServletRequest request)
    {
        boolean result = emailServices.sendInviteEmail(invitation, request);
        if (result)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("INVITE SENT", AppConstants.SUCCESS));
        else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("TASK FAILED", AppConstants.TASK_FAILED));
    }

    @GetMapping("/available-organizers")
    public ResponseEntity<?> getAllOrganizers(){
        List<?> organizers = recruiterServices.getAllOrganizers();
        if(organizers.size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(organizers, AppConstants.NO_RESULT_SUCCESS));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(organizers, AppConstants.SUCCESS));
    }

    @GetMapping("/organizers")
    public ResponseEntity<?> getOrganizersList(@RequestParam (required = false) Integer limit, HttpServletRequest request)
    {
        if(!memberServices.validPageDetails(1, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID LIMIT", AppConstants.INVALID_INFORMATION));
        }
        List<?> organizers = recruiterServices.getOrganizers(limit, request);
        if(organizers.size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(organizers, AppConstants.NO_RESULT_SUCCESS));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(organizers, AppConstants.SUCCESS));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestParam(required = false) Date date, HttpServletRequest request)
    {
        Summary summary = recruiterServices.getSummary(date, request);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(summary, AppConstants.SUCCESS));
    }

    @GetMapping("/cv-count")
    public ResponseEntity<?> getCVCount(HttpServletRequest request)
    {
        int count = recruiterServices.cvCount(request);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(count, AppConstants.SUCCESS));
    }

    @GetMapping("/cv-analysis")
    public ResponseEntity<?> getCVAnalysis (@RequestParam(required = false) Date date, @RequestParam int pageNo, int limit)
    {
        if(!memberServices.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID PAGE DETAILS", AppConstants.INVALID_INFORMATION));
        }
        PageData<?> CVAnalyses = recruiterServices.cvAnalysisPage(date, pageNo, limit);
        if(CVAnalyses == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>(null, AppConstants.SUCCESS));

        else if(CVAnalyses.getData().size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(CVAnalyses, AppConstants.NO_RESULT_SUCCESS));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(CVAnalyses, AppConstants.SUCCESS));

    }

    @GetMapping("/search/{designation}")
    public ResponseEntity<?> search(@PathVariable String designation)
    {
        CvAnalysis cvAnalysis = recruiterServices.searchDesignation(designation);
        if(cvAnalysis == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData("NOT FOUND", AppConstants.RECORD_NOT_EXIST));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData(cvAnalysis, AppConstants.SUCCESS));

    }

    @PatchMapping("/update-position-status")
    public ResponseEntity<?> updatePositionStatus(@RequestParam String designation, @RequestParam String newStatus) {
        int rowsUpdated = recruiterServices.updateStatus(designation, newStatus);
        if (rowsUpdated > 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("UPDATED", AppConstants.SUCCESS));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>("FAILED", AppConstants.RECORD_NOT_EXIST));
    }

    @GetMapping("/top-technologies/{designation}")
    public ResponseEntity<?> getTopTechnologies(@PathVariable String designation) {
        List<TopTechnology> technologies = recruiterServices.getTopTechnologies(designation);
        if(technologies.get(0).getLocation().size()==0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>("NOT FOUND", AppConstants.RECORD_NOT_EXIST));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(technologies, AppConstants.SUCCESS));
    }

    @GetMapping("/extended-cv/{candidateId}")
    public ResponseEntity<?> getExtendedCV(@PathVariable int candidateId,HttpServletRequest request){
        ExtendedCV extendedCV = recruiterServices.getBasicCVDetails(candidateId, request);
        if(extendedCV == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>("NOT FOUND", AppConstants.RECORD_NOT_EXIST));
        }
        extendedCV.setEducations(recruiterServices.getEducationsHistory(candidateId));
        extendedCV.setWorkHistories(recruiterServices.getWorkHistory(candidateId));
        extendedCV.setLinks(recruiterServices.getSocialLinks(candidateId));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(extendedCV, AppConstants.SUCCESS));
    }

    @GetMapping("/resume-url/{candidateId}")
    public ResponseEntity<?> getResumeDownloadUrl(@PathVariable int candidateId, HttpServletRequest request){
        String url = recruiterServices.downloadCV(candidateId, request);
        if(url.equals(""))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>("NO RECORD FOUND", AppConstants.RECORD_NOT_EXIST));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(url, AppConstants.SUCCESS));
    }

    @GetMapping("/profiles/{designation}/{status}")
    public ResponseEntity<?> getProfileBasedOnStatus(@PathVariable String designation, @PathVariable String status, @RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request) {
        if(!memberServices.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID PAGE DETAILS", AppConstants.INVALID_INFORMATION));
        }
        PageData<?> pageData = recruiterServices.getProfileBasedOnStatus(designation, status, pageNo, limit, request);
        if(pageData == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>(null, AppConstants.RECORD_NOT_EXIST));

        else if(pageData.getData().size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(pageData, AppConstants.NO_RESULT_SUCCESS));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(pageData, AppConstants.SUCCESS));

    }

    @GetMapping("/applicants")
    public ResponseEntity<?> getApplicants(HttpServletRequest request)
    {
        List<Application> applications = recruiterServices.getNotAssignedApplicants(request);
        if(applications.size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(applications, AppConstants.NO_RESULT_SUCCESS));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(applications, AppConstants.SUCCESS));
    }

    @PutMapping("/organizer-assignation")
    public ResponseEntity<?> setOrganizer(@RequestBody AssignBoard assignBoard, HttpServletRequest request)
    {
        ResponseData result = recruiterServices.assignOrganizer(assignBoard, request);
        if(result.getResult().getOpinion().equals("F"))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(result);
        else
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @GetMapping("/assignboard")
    public ResponseEntity<?> getPage(@RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberServices.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID PAGE DETAILS", AppConstants.INVALID_INFORMATION));
        }
        PageData<?> assignBoard = recruiterServices.getAssignBoardPage(pageNo, limit, request);

        if(assignBoard == null)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("FAILED", AppConstants.TASK_FAILED));

        else if(assignBoard.getData().size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(assignBoard, AppConstants.NO_RESULT_SUCCESS));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(assignBoard, AppConstants.SUCCESS));

    }

    @GetMapping("/rejected-cv")
    public ResponseEntity<?> getCvPage(@RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberServices.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID PAGE DETAILS", AppConstants.INVALID_INFORMATION));
        }

        PageData rejectedCvs = recruiterServices.getRejectedCvPage(pageNo, limit, request);
        if(rejectedCvs == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>(null, AppConstants.RECORD_NOT_EXIST));
        }
        else if(rejectedCvs.getData().size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(rejectedCvs, AppConstants.NO_RESULT_SUCCESS));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(rejectedCvs, AppConstants.SUCCESS));
    }

    @GetMapping("/invites-info")
    public ResponseEntity<?> getInfo(HttpServletRequest request)
    {
        Invite invite = recruiterServices.getInviteInfo(request);

        if(invite == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>(null, AppConstants.RECORD_NOT_EXIST));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(invite, AppConstants.SUCCESS));
    }

    @GetMapping("/invites-by-day")
    public ResponseEntity<?> getByDay(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberServices.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID PAGE DETAILS", AppConstants.INVALID_INFORMATION));
        }
        PageData<?> invites = recruiterServices.getByDay(date, pageNo, limit, request);

        if(invites == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>(null, AppConstants.RECORD_NOT_EXIST));
        }
        else if(invites.getData().size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(invites, AppConstants.NO_RESULT_SUCCESS));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(invites, AppConstants.SUCCESS));
    }

    @GetMapping("/invites-by-month")
    public ResponseEntity<?> getByMonth(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberServices.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID PAGE DETAILS", AppConstants.INVALID_INFORMATION));
        }
        PageData<?> invites = recruiterServices.getByMonth(date, pageNo, limit, request);
        if(invites == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>(null, AppConstants.RECORD_NOT_EXIST));
        }
        else if(invites.getData().size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(invites, AppConstants.NO_RESULT_SUCCESS));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(invites, AppConstants.SUCCESS));
    }

    @GetMapping("/invites-by-year")
    public ResponseEntity<?> getByYear(@RequestParam Date date, @RequestParam int pageNo, @RequestParam int limit, HttpServletRequest request)
    {
        if(!memberServices.validPageDetails(pageNo, limit)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("INVALID PAGE DETAILS", AppConstants.INVALID_INFORMATION));
        }
        PageData<?> invites = recruiterServices.getByYear(date, pageNo, limit, request);
        if(invites == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData<>(null, AppConstants.RECORD_NOT_EXIST));
        }
        else if(invites.getData().size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(invites, AppConstants.NO_RESULT_SUCCESS));

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(invites, AppConstants.SUCCESS));
    }

    @PutMapping("/resend-invite")
    public ResponseEntity<?> resendInvite(@RequestParam int inviteId, HttpServletRequest request)
    {
        boolean result = emailServices.resendInvite(inviteId, request);
        if (result)
        {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>("INVITE SENT", AppConstants.SUCCESS));
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("TASK FAILED", AppConstants.TASK_FAILED));
    }

    @GetMapping("/invitation-search")
    public ResponseEntity<?> searchInvites(@RequestParam int value,@RequestParam Date date, @RequestParam String name, HttpServletRequest request)
    {
        List<SentInvite> list = recruiterServices.searchInvites(value, date, name, request);
        if (list==null)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData<>(null, AppConstants.RECORD_NOT_EXIST));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(list, AppConstants.SUCCESS));
    }

}

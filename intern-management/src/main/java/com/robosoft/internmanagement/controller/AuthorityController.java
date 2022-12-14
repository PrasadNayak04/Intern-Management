package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.constants.AppConstants;
import com.robosoft.internmanagement.exception.ResponseData;
import com.robosoft.internmanagement.model.Application;
import com.robosoft.internmanagement.model.MemberModel;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.modelAttributes.Technology;
import com.robosoft.internmanagement.service.AuthorityServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/intern-management/authority")
public class AuthorityController {
    @Autowired
    AuthorityServices authorityServices;

    @PostMapping("/new-technology")
    public ResponseEntity<?> addNewTechnology(@RequestBody Technology technology, HttpServletRequest request) {
        ResponseData<?> responseData = authorityServices.addTechnology(technology, request);

        if (responseData.getResult().getOpinion().equals("T"))
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseData);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseData);
    }

    @GetMapping("/available-recruiters")
    public ResponseEntity<?> getAllRecruiters() {
        List<MemberModel> memberModels = authorityServices.getAllRecruiters();
        if(memberModels.size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(memberModels, AppConstants.NO_RESULT_SUCCESS));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(memberModels, AppConstants.SUCCESS));
    }

    @GetMapping("/applicants")
    public ResponseEntity<?> allApplicants() {
        List<Application> applications = authorityServices.getApplicants();
        if(applications.size() == 0)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(applications, AppConstants.NO_RESULT_SUCCESS));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(applications, AppConstants.SUCCESS));
    }

    @PostMapping(value = "/recruiter-assignation")
    public ResponseEntity<?> setRecruiter(@RequestBody AssignBoard assignBoard, HttpServletRequest request) {
        ResponseData<?> responseData = authorityServices.assignRecruiter(assignBoard, request);

        if (responseData.getResult().getOpinion().equals("T"))
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseData);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseData);
    }

}


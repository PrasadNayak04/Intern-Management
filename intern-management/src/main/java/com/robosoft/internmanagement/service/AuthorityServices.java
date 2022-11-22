package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.model.Application;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;

import java.util.List;

public interface AuthorityServices
{
    List<?> getAllRecruiters();

    List<Application> getApplicants();

    String assignRecruiter(AssignBoard assignBoard);
}

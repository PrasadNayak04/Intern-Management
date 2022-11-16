package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.modelAttributes.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.modelAttributes.CandidateInvites;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecruiterService
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    String query;
    public List<Organizer> getOrganizer()
    {
        query = "select memberProfile.name,memberProfile.photoUrl,count(assignBoard.organizerEmail) as interviews from memberProfile inner join assignBoard on memberProfile.emailId=assignBoard.organizerEmail";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Organizer.class));
    }

    public int getInterviewsCount(String organizerEmail, String emailId){
        query = "select count(*) from AssignBoard where OrganizerEmail = ? and recruiterEmail = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, organizerEmail, MemberService.getCurrentUser());
    }

    public Summary getSummary()
    {
        Summary summary = new Summary();
        query = "select count(*) from assignBoard where year(AssignDate)=year(curDate()) and month(assignDate)=month(curDate()) and status = ? and recruiterEmail=?";
        int shortlisted = jdbcTemplate.queryForObject(query, Integer.class,"Shortlisted",MemberService.getCurrentUser());
        summary.setShortlisted(shortlisted);
        query = "select count(*) from assignBoard where year(assignDate)=year(curDate()) and month(assignDate)=month(curDate()) and status=? and recruiterEmail=?";
        int onHold = jdbcTemplate.queryForObject(query, Integer.class,"New",MemberService.getCurrentUser());
        summary.setOnHold(onHold);
        query = "select count(*) from assignBoard where year(assignDate)=year(curDate()) and month(assignDate)=month(curDate()) and status=? and recruiterEmail=?";
        int rejected = jdbcTemplate.queryForObject(query, Integer.class,"Rejected",MemberService.getCurrentUser());
        summary.setRejected(rejected);
        int applications=shortlisted + onHold + rejected;
        summary.setApplications(applications);
        return summary;
    }

    public int cvCount() //w
    {
        query = "select count(applicationId) from assignBoard where recruiterEmail=? and organizerEmail is null";
        return jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser());
    }

    public LoggedProfile getProfile()
    {
        query = "select name,designation,photoUrl from memberProfile where emailId=?";
        return jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(LoggedProfile.class),MemberService.getCurrentUser());
    }

    public NotificationDisplay notification() {
        String notification = "select message from notifications where emailId=? limit 1";
        String notificationType = jdbcTemplate.queryForObject("select type from notifications where emailId=? limit 1", String.class, MemberService.getCurrentUser());
        int eventId = jdbcTemplate.queryForObject("select eventId from notifications where emailId=? limit 1", Integer.class, MemberService.getCurrentUser());
        if (notificationType.equalsIgnoreCase("Invite")) {
            String profileImage = "select photoPath from memberProfile,notifications,events,eventInvites where notifications.emailId=events.creatorEmail and events.eventId=eventInvites.eventId and eventInvites.invitedEmail=memberProfile.emailId and notifications.emailId=? and notifications.eventId=?";
            List<String> Images = jdbcTemplate.query(profileImage, new BeanPropertyRowMapper<>(String.class),MemberService.getCurrentUser(), eventId);
            NotificationDisplay display = jdbcTemplate.queryForObject(notification, new BeanPropertyRowMapper<>(NotificationDisplay.class), MemberService.getCurrentUser());
            display.setImages(Images);
            return display;
        } else {
            NotificationDisplay display = jdbcTemplate.queryForObject(notification, new BeanPropertyRowMapper<>(NotificationDisplay.class), MemberService.getCurrentUser());
            return display;
        }
    }

    public List<CvAnalysis> cvAnalysisPage()
    {
        query = "select applications.designation,count(applications.designation),date,status,location from applications,technologies where applications.designation = technologies.designation and date=curDate() group by designation";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(CvAnalysis.class));
    }

    public CvAnalysis searchDesignation(String designation)
    {
        query  = "select applications.designation,application.count(designation),date,status,location from applications,technologies where applications.designation = technologies.designation and date=curDate() and designation=? group by designation";
        return jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(CvAnalysis.class),designation);
    }

    public List<Applications> getNotAssignedApplicants()
    {
        query = "select applications.applicationId,emailId,designation,location,date from applications,assignBoard where applications.applicationId=assignBoard.applicationId and organizerEmail is null";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Applications.class));
    }

    public String assignOrganizer(AssignBoard assignBoard)
    {
        try {
            query = "select name from memberProfile where emailId=? and position=?";
            jdbcTemplate.queryForObject(query, String.class,assignBoard.getOrganizerEmail(),"Organizer");

            try {
                query = "update assignBoard set organizerEmail =?, assignDate=curDate() where recruiterEmail=? and applicationId=?";
                jdbcTemplate.update(query,assignBoard.getOrganizerEmail(),MemberService.getCurrentUser(),assignBoard.getApplicationId());
                return "Candidate assigned successfully";
            }
            catch (Exception e)
            {
                 return "Give correct information";
            }
        } catch (Exception e) {
            return "Select correct Organizer to assign";
        }
    }

    public List<AssignBoardPage> getAssignBoardPage()
    {
        query = "select candidateProfile.name,applications.designation,applications.location,assignBoard.assignDate,memberProfile.name as organizer  from memberProfile inner join assignBoard on memberProfile.emailId=assignBoard.organizerEmail inner join applications on assignBoard.applicationId=applications.applicationId inner join candidateProfile on candidateProfile.emailId=applications.emailId where recruiterEmail=?";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(AssignBoardPage.class),MemberService.getCurrentUser());
    }

    public List<RejectedCv> getRejectedCvPage()
    {
        query = "select candidateProfile.name,documents.ImageUrl,applications.designation,applications.location,candidateProfile.mobileNumber from documents inner join candidateProfile on documents.emailId=candidateProfile.emailId inner join applications on candidateProfile.emailId=applications.emailId inner join assignBoard on applications.applicationId=assignBoard.applicationId where assignBoard.status=?";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(RejectedCv.class),"Rejected");
    }

//    public Invite getList()
//    {
//        Invite invite=new Invite();
//        query = "select count(*) from candidateInvites where date=curDate() and fromEmail=?";
//        int today = jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser());
//        invite.setToday(today);
//        query = "select count(*) from candidateInvites where date=DATE_SUB(CURDATE(),INTERVAL 1 DAY) and fromEmail=?";
//        int yesterday = jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser());
//        invite.setYesterday(yesterday);
//        //query = "select count(*) from candidateInvites where date"
//    }

}

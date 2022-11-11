package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.modelAttributes.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
        List<Organizer> organizerList = new ArrayList<>();
        query = "select memberProfile.name, memberProfile.emailId, memberProfile.photoPath from memberProfile, AssignBoard where memberProfile.emailId = AssignBoard.organizerEmail and assignBoard.recruiterEmail = '" + MemberService.getCurrentUser() + "'";

        jdbcTemplate.query(query,
                (resultSet, no) -> {
                    Organizer organizer = new Organizer();
                    organizer.setName(resultSet.getString(1));
                    organizer.setProfile(resultSet.getString(3));
                    organizer.setInterviews(getInterviewsCount(resultSet.getString(2), MemberService.getCurrentUser()));

                    organizerList.add(organizer);
                    return organizer;
                });
        return organizerList;
    }

    public int getInterviewsCount(String organizerEmail, String emailId){
        query = "select count(*) from AssignBoard where OrganizerEmail = ? and recruiterEmail = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, organizerEmail, MemberService.getCurrentUser());
    }

    public Summary getSummary()
    {
        Summary summary=null;
        query = "select count(*) from assignBoard where year(date)=year(curDate()) and month(date)=month(curDate()) and recruiterMail=?";
        int shortlisted = jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser());
        summary.setShortlisted(shortlisted);
        query = "select count(*) from assignBoard where year(date)=year(curDate()) and month(date)=month(curDate()) and recruiterMail=?";
        int onHold = jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser());
        summary.setOnHold(onHold);
        query = "select count(*) from assignBoard where year(date)=year(curDate) and month(date)=month(curDate) and recruiterMail=?";
        int rejected = jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser());
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
        query = "select name,designation,profilePath from memberProfile where emailId=?";
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
        query = "select applications.designation,application.count(designation),date,status,location from applications,technologies where applications.designation = technologies.designation and date=curDate() group by designation";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(CvAnalysis.class));
    }

    public CvAnalysis searchDesignation(String designation)
    {
        query  = "select applications.designation,application.count(designation),date,status,location from applications,technologies where applications.designation = technologies.designation and date=curDate() and designation=? group by designation";
        return jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(CvAnalysis.class),designation);
    }

    public List<Applications>  getNotAssignedApplicants()
    {
        query = "select applications.applicationId,emailId,designation,location,date from applications,assignBoard where applications.applicationId=assignBoard.applicationId and organizerEmail is null";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Applications.class));
    }

    public String assignOrganizer(AssignBoard assignBoard)
    {
        System.out.println(MemberService.getCurrentUser());
        System.out.println(MemberService.getCurrentUser());
        try
        {
            query = "select name from memberProfile where emailId=? and position=?";
            jdbcTemplate.queryForObject(query, String.class,assignBoard.getOrganizerEmail(),"Organizer");

            try
            {
                query = "update assignBoard set organizerEmail =?, assignDate=curDate() where recruiterEmail=? and applicationId=?";
                jdbcTemplate.update(query,assignBoard.getOrganizerEmail(),MemberService.getCurrentUser(),assignBoard.getApplicationId());
                return "Candidate assigned successfully";
            }
            catch (Exception e)
            {
                e.printStackTrace();
                 return "Give correct information";
            }
            }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Select correct Organizer to assign";
        }
    }

}

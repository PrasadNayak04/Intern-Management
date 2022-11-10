package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.model.*;
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
    public List<Organizer> getOrganizer(String emailId)
    {
        List<Organizer> organizerList = new ArrayList<>();
        query = "select memberProfile.name, memberProfile.emailId, memberProfile.photoPath from memberProfile, AssignBoard where memberProfile.emailId = AssignBoard.organizerEmail and assignBoard.recruiterEmail = '" + emailId + "'";

        jdbcTemplate.query(query,
                (resultSet, no) -> {
                    Organizer organizer = new Organizer();
                    organizer.setName(resultSet.getString(1));
                    organizer.setProfile(resultSet.getString(3));
                    organizer.setInterviews(getInterviewsCount(resultSet.getString(2), emailId));

                    organizerList.add(organizer);
                    return organizer;
                });
        return organizerList;
    }

    public int getInterviewsCount(String organizerEmail, String recruiterEmail){
        query = "select count(*) from AssignBoard where OrganizerEmail = ? and recruiterEmail = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, organizerEmail, recruiterEmail);
    }

    public Summary getSummary(String emailId)
    {
        Summary summary=null;
        query = "select count(*) from assignBoard where year(date)=year(curDate()) and month(date)=month(curDate()) and recruiterMail=?";
        int shortlisted = jdbcTemplate.queryForObject(query, Integer.class,emailId);
        summary.setShortlisted(shortlisted);
        query = "select count(*) from assignBoard where year(date)=year(curDate()) and month(date)=month(curDate()) and recruiterMail=?";
        int onHold = jdbcTemplate.queryForObject(query, Integer.class,emailId);
        summary.setOnHold(onHold);
        query = "select count(*) from assignBoard where year(date)=year(curDate) and month(date)=month(curDate) and recruiterMail=?";
        int rejected = jdbcTemplate.queryForObject(query, Integer.class,emailId);
        summary.setRejected(rejected);
        int applications=shortlisted + onHold + rejected;
        summary.setApplications(applications);
        return summary;
    }

    public int cvCount(String emailId)
    {
        query = "select count(applicationId) from assignBoard where recruiterEmail=? and organizerEmail=?";
        return jdbcTemplate.queryForObject(query, Integer.class,emailId,null);
    }

    public LoggedProfile getProfile(String emailId)
    {
        query = "select name,designation,profilePath from memberProfile where emailId=?";
        return jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(LoggedProfile.class),emailId);
    }

    public NotificationDisplay notification(String emailId) {
        String notification = "select message from notifications where emailId=? limit 1";
        String notificationType = jdbcTemplate.queryForObject("select type from notifications where emailId=? limit 1", String.class, emailId);
        int eventId = jdbcTemplate.queryForObject("select eventId from notifications where emailId=? limit 1", Integer.class, emailId);
        if (notificationType.equalsIgnoreCase("Invite")) {
            String profileImage = "select photoPath from memberProfile,notifications,events,eventInvites where notifications.emailId=events.creatorEmail and events.eventId=eventInvites.eventId and eventInvites.invitedEmail=memberProfile.emailId and notifications.emailId=? and notifications.eventId=?";
            List<String> Images = jdbcTemplate.query(profileImage, new BeanPropertyRowMapper<>(String.class), emailId, eventId);
            NotificationDisplay display = jdbcTemplate.queryForObject(notification, new BeanPropertyRowMapper<>(NotificationDisplay.class), emailId);
            display.setImages(Images);
            return display;
        } else {
            NotificationDisplay display = jdbcTemplate.queryForObject(notification, new BeanPropertyRowMapper<>(NotificationDisplay.class), emailId);
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
}

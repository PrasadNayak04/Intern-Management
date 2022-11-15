package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.modelAttributes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

    public int cvCount()
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

    public List<CvAnalysis> cvAnalysisPage(Date date)
    {
        List<CvAnalysis> cvAnalysisList = new ArrayList<>();
        if(date == null){
            date = Date.valueOf(LocalDate.now());
        }
        query = "select applications.designation,count(applications.designation),date,status from applications,technologies where applications.designation = technologies.designation and date=? group by designation";
        jdbcTemplate.query(query,
                (resultSet, no) -> {
                    CvAnalysis cvAnalysis = new CvAnalysis();

                    cvAnalysis.setDesignation(resultSet.getString(1));
                    cvAnalysis.setApplicants(resultSet.getInt(2));
                    cvAnalysis.setReceivedDate(resultSet.getDate(3));
                    cvAnalysis.setStatus(resultSet.getString(4));
                    cvAnalysis.setLocations(getLocationsByDesignation(resultSet.getString(1)));

                    cvAnalysisList.add(cvAnalysis);
                    return cvAnalysis;
                }, date);
        return cvAnalysisList;
    }

    public CvAnalysis searchDesignation(String designation)
    {
        query  = "select applications.designation,count(applications.designation),date,status from applications,technologies where applications.designation = technologies.designation and applications.designation=? group by applications.designation";
        try {
            return jdbcTemplate.queryForObject(query,
                    (resultSet, no) -> {
                        CvAnalysis cvAnalysis = new CvAnalysis();
                        cvAnalysis.setDesignation(resultSet.getString(1));
                        cvAnalysis.setApplicants(resultSet.getInt(2));
                        cvAnalysis.setReceivedDate(resultSet.getDate(3));
                        cvAnalysis.setStatus(resultSet.getString(4));
                        cvAnalysis.setLocations(getLocationsByDesignation(designation));
                        return cvAnalysis;
                    }, designation);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public int updateStatus(String designation, String newStatus){
        query = "update Technologies set status = ? where designation = ?";
        return jdbcTemplate.update(query, newStatus, designation);
    }

    public List<String> getLocationsByDesignation(String designation){
        query = "select location from location where designation = ?";
        return jdbcTemplate.queryForList(query, String.class, designation);
    }

    public ExtendedCV getBasicCVDetails(String emailId){
        try{
            query = "select name, dob, mobileNumber, CandidateProfile.emailId, jobLocation, position, expYear, expMonth, candidateType, contactPerson, languagesKnown, softwaresWorked, skills, about, expectedCTC, attachmentUrl, imageUrl from CandidateProfile inner join documents using(EmailId) inner join  applications  using(emailId) inner join assignboard using(applicationId)  where assignboard.recruiterEmail= ?  and  documents.emailId = ?";
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ExtendedCV.class), MemberService.getCurrentUser(), emailId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Education> getEducationsHistory(String emailId){
        query = "select * from Education where emailId = ?";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Education.class), emailId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<WorkHistory> getWorkHistory(String emailId){
        query = "select * from WorkHistory where emailId = ?";
        try{
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(WorkHistory.class), emailId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Link> getSocialLinks(String emailId){
        query = "select * from Links where emailId = ?";
        try{
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Link.class), emailId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public String downloadCV(String emailId){
        query = "select attachmentUrl from documents where emailId = ?";
        try{
            return jdbcTemplate.queryForObject(query, String.class, emailId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<TopTechnologies> getTopTechnologies(String designation) {
        query = "select technologies.designation,location.location from technologies left join location using(designation) left join applications using(designation) where designation != ? group by technologies.designation order by count(applications.designation) desc limit 5";
        List<TopTechnologies> topTechnologies = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TopTechnologies.class),designation);
        List<String> locations = getLocationsByDesignation(designation);
        int size = locations.size();
        TopTechnologies technologies = new TopTechnologies(designation,locations);
        topTechnologies.add(0,technologies);
        return topTechnologies;
    }

    public String getLastJobPosition(String emailId) {
        System.out.println("yo");
        query = "select position from workHistory where emailId = ? order by fromDate desc";
        List<String> positions = jdbcTemplate.queryForList(query,String.class,emailId);
        return positions.get(0);
    }
    public List<ProfileAnalysis> getProfileBasedOnStatus(String designation, String status) {
        query = "select candidateprofile.name,documents.imageUrl,candidateprofile.emailId,candidateprofile.skills from assignboard inner join applications using(applicationId) inner join candidateprofile using(emailId) inner join documents using(emailId) where recruiterEmail = ? and assignboard.status = ? and applications.designation = ?";
        List<ProfileAnalysis> profileAnalyses = new ArrayList<>();
        try {
            return jdbcTemplate.query(query,
                    (resultSet, no) -> {
                        ProfileAnalysis profileAnalysis = new ProfileAnalysis();
                        profileAnalysis.setName(resultSet.getString(1));
                        profileAnalysis.setImageUrl(resultSet.getString(2));
                        profileAnalysis.setPosition(getLastJobPosition(resultSet.getString(3)));
                        profileAnalysis.setSkills(resultSet.getString(4));
                        profileAnalyses.add(profileAnalysis);
                        return profileAnalysis;
                    }, MemberService.getCurrentUser(), status, designation);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
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

}

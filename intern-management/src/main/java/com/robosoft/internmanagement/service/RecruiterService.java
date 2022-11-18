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
import java.sql.ResultSet;
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

    public List<Organizer> getOrganizer()
    {
        String status = "new";
        List<Organizer> organizerList = new ArrayList<>();
        query = "select memberProfile.name, memberProfile.emailId, memberProfile.photoUrl from memberProfile inner join AssignBoard on memberProfile.emailId = AssignBoard.organizerEmail where assignBoard.recruiterEmail = '" +MemberService.getCurrentUser()+ "' and assignBoard.status='" + status + "' group by assignBoard.recruiterEmail";

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

    public int getInterviewsCount(String organizerEmail, String recruiterEmail){
        query = "select count(*) from AssignBoard where OrganizerEmail = ? and recruiterEmail = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, organizerEmail, recruiterEmail);
    }

    public Summary getSummary(Date date)
    {
        Summary summary = new Summary();
        query = "select count(*) from assignBoard where month(assignDate)=? and year(assignDate)= ? and status = ? and recruiterEmail=?";
        int shortlisted = jdbcTemplate.queryForObject(query, Integer.class,date.toLocalDate().getMonthValue(),date.toLocalDate().getYear(),"Shortlisted",MemberService.getCurrentUser());
        summary.setShortlisted(shortlisted);
        query = "select count(*) from assignBoard where month(assignDate)=? and year(assignDate)=? and status=? and recruiterEmail=?";
        int onHold = jdbcTemplate.queryForObject(query, Integer.class,date.toLocalDate().getMonthValue(),date.toLocalDate().getYear(),"New",MemberService.getCurrentUser());
        summary.setOnHold(onHold);
        query = "select count(*) from assignBoard where month(assignDate)=? and year(assignDate)=? and status=? and recruiterEmail=?";
        int rejected = jdbcTemplate.queryForObject(query, Integer.class,date.toLocalDate().getMonthValue(),date.toLocalDate().getYear(),"Rejected",MemberService.getCurrentUser());
        summary.setRejected(rejected);
        int assigned = jdbcTemplate.queryForObject(query, Integer.class,"Assigned",MemberService.getCurrentUser());
        int applications=shortlisted + onHold + rejected + assigned;
        summary.setApplications(applications);
        return summary;
    }

    public int cvCount()
    {
        query = "select count(applicationId) from assignBoard where recruiterEmail=? and organizerEmail is null and deleted = 0";
        return jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser());
    }

    public LoggedProfile getProfile()
    {
        query = "select name,designation,photoUrl from memberProfile where emailId=?";
        return jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(LoggedProfile.class),MemberService.getCurrentUser());
    }

    public NotificationDisplay notification() {
        String notification = "select message from notifications where emailId=? and deleted = 0 limit 1";
        String notificationType = jdbcTemplate.queryForObject("select type from notifications where emailId=? and deleted = 0 limit 1", String.class, MemberService.getCurrentUser());
        int eventId = jdbcTemplate.queryForObject("select eventId from notifications where emailId=? and deleted = 0 limit 1", Integer.class, MemberService.getCurrentUser());
        if (notificationType.equalsIgnoreCase("Invite")) {
            String profileImage = "select photoPath from memberProfile,notifications,events,eventInvites where notifications.emailId=events.creatorEmail and events.eventId=eventInvites.eventId and eventInvites.invitedEmail=memberProfile.emailId and notifications.emailId=? and notifications.eventId=? and memberProfile.deleted = 0 and Notifications.deleted = 0 events.deleted = 0 eventInvites.deleted = 0";
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
        query = "select applications.designation,count(applications.designation),date,status from applications,technologies where applications.designation = technologies.designation and date=? and applications.deleted = 0 and technologies.deleted = 0 group by designation";
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
        query  = "select applications.designation,count(applications.designation),date,status from applications,technologies where applications.designation = technologies.designation and applications.designation=? and applications.deleted = 0 and technologies.deleted = 0 group by applications.designation";
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
        query = "update Technologies set status = ? where designation = ? and deleted = 0";
        return jdbcTemplate.update(query, newStatus, designation);
    }

    public List<String> getLocationsByDesignation(String designation){
        query = "select location from location where designation = ? and deleted = 0";
        return jdbcTemplate.queryForList(query, String.class, designation);
    }

    public ExtendedCV getBasicCVDetails(int applicationId){
        try{
            query = "select " + applicationId + " as applicationId, name, dob, mobileNumber, CandidateProfile.emailId, jobLocation, position, expYear, expMonth, candidateType, contactPerson, languagesKnown, softwaresWorked, skills, about, expectedCTC, attachmentUrl, imageUrl from CandidateProfile inner join documents using(date) inner join applications using(date) inner join assignboard using(applicationId)  where assignboard.recruiterEmail= ? and applications.applicationId = ? and CandidateProfile.deleted = 0 and documents.deleted = 0 and Applications.deleted = 0 and AssignBoard.deleted = 0";
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ExtendedCV.class), MemberService.getCurrentUser(), applicationId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Education> getEducationsHistory(int applicationId){
        query = "select * from Education inner join applications using(date) where applicationId = ? and applications.deleted = 0 and education.deleted = 0";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Education.class), applicationId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<WorkHistory> getWorkHistory(int applicationId){
        query = "select * from WorkHistory inner join applications using(date) where applicationId = ? and applications.deleted = 0 and workHistory.deleted = 0";
        try{
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(WorkHistory.class), applicationId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Link> getSocialLinks(int applicationId){
        query = "select * from Links inner join applications using(date) where applicationId = ? and applications.deleted = 0 and Links.deleted = 0";
        try{
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Link.class), applicationId);
        } catch (Exception e) {
            return null;
        }
    }

    public String downloadCV(int applicationId){
        query = "select attachmentUrl from documents inner join applications using(date) where applicationId = ? and applications.deleted = 0 and documents.deleted = 0";
        try{
            return jdbcTemplate.queryForObject(query, String.class, applicationId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<TopTechnologies> getTopTechnologies(String designation) {
        query = "select technologies.designation,location.location from technologies left join location using(designation) left join applications using(designation) where designation != ? and Technologies.deleted = 0 and Location.deleted = 0 and Applications.deleted = 0 group by technologies.designation order by count(applications.designation) desc limit 5";
        List<TopTechnologies> topTechnologies = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TopTechnologies.class),designation);
        List<String> locations = getLocationsByDesignation(designation);
        TopTechnologies technologies = new TopTechnologies(designation,locations);
        topTechnologies.add(0,technologies);
        return topTechnologies;
    }

    public String getLastJobPosition(int applicationId) {
        System.out.println("yo");
        query = "select position from workHistory inner join applications using(date) where date = (select date from applications where applicationId = ?) order by fromDate desc";
        List<String> positions = jdbcTemplate.queryForList(query,String.class,applicationId);
        return positions.get(0);
    }
    public List<ProfileAnalysis> getProfileBasedOnStatus(String designation, String status) {
        query = "select Applications.applicationId, CandidateProfile.name, imageUrl, skills, position from CandidateProfile inner join documents using(date) inner join Applications using(date) inner join Assignboard using(applicationId) where recruiterEmail = ? and assignboard.status = ? and applications.designation = ? and CandidateProfile.deleted = 0 and Documents.deleted = 0 and Assignboard.deleted = 0 and Applications.deleted = 0 group by Applications.applicationId";
        //select applications.applicationId, candidateprofile.name,documents.imageUrl,candidateprofile.emailId,candidateprofile.skills from assignboard inner join applications using(applicationId) inner join candidateprofile using(date) inner join documents using(date) where recruiterEmail = ? and assignboard.status = ? and applications.designation = ?
        List<ProfileAnalysis> profileAnalyses = new ArrayList<>();
        try {
            return jdbcTemplate.query(query,
                    (resultSet, no) -> {
                        ProfileAnalysis profileAnalysis = new ProfileAnalysis();
                        profileAnalysis.setApplicationId(resultSet.getInt(1));
                        profileAnalysis.setName(resultSet.getString(2));
                        profileAnalysis.setImageUrl(resultSet.getString(3));
                        profileAnalysis.setPosition(getLastJobPosition(profileAnalysis.getApplicationId()));
                        profileAnalysis.setSkills(resultSet.getString(5));
                        profileAnalyses.add(profileAnalysis);
                        return profileAnalysis;
                    }, MemberService.getCurrentUser(), status, designation);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Applications> getNotAssignedApplicants()
    {
        query = "select applications.applicationId,emailId,designation,location,date from applications,assignBoard where applications.applicationId=assignBoard.applicationId and organizerEmail is null and applications.deleted = 0 and assignboard.deleted = 0";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Applications.class));
    }

    public String assignOrganizer(AssignBoard assignBoard)
    {
        try {
            query = "Select count(*) from AssignBoard where ApplicationId = ? and recruiterEmail = ? and status = 'rejected' and deleted = 0";
            int count = jdbcTemplate.queryForObject(query, Integer.class, assignBoard.getApplicationId(), MemberService.getCurrentUser());
            if(count == 0) {
                query = "select name from memberProfile where emailId=? and position=?";
                jdbcTemplate.queryForObject(query, String.class, assignBoard.getOrganizerEmail(), "Organizer");
                query = "select applicationId from assignBoard where recruiterEmail=? and applicationId=?";
                jdbcTemplate.queryForObject(query, Integer.class, MemberService.getCurrentUser(), assignBoard.getApplicationId());
                try {
                    query = "update assignBoard set organizerEmail =?, assignDate=curDate() where recruiterEmail=? and applicationId=?";
                    jdbcTemplate.update(query, assignBoard.getOrganizerEmail(), MemberService.getCurrentUser(), assignBoard.getApplicationId());
                    return "Candidate assigned successfully";
                } catch (Exception e) {
                    return "Give correct information";
                }
            }
            else{
                query = "update assignBoard set status='new' where ApplicationId = ? and recruiterEmail = ? and status = 'rejected' and deleted = 0";
                jdbcTemplate.update(query, assignBoard.getApplicationId(),MemberService.getCurrentUser());
                return "Updated";
            }
        } catch (Exception e) {
            return "Select correct Recruiter/Organizer to assign";
        }
    }

    public List<AssignBoardPage> getAssignBoardPage()
    {
        query = "select candidateProfile.name,applications.designation,applications.location,assignBoard.assignDate,memberProfile.name as organizer  from memberProfile inner join assignBoard on memberProfile.emailId=assignBoard.organizerEmail inner join applications on assignBoard.applicationId=applications.applicationId inner join candidateProfile on candidateProfile.emailId=applications.emailId where recruiterEmail=? and MemberProfile.deleted = 0 and CandidateProfile.deleted = 0 and Assignboard.deleted = 0 and Applications.deleted = 0 group by Applications.applicationId";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(AssignBoardPage.class),MemberService.getCurrentUser());
    }

    public List<RejectedCv> getRejectedCvPage()
    {
        query = "select candidateProfile.name,documents.ImageUrl,candidateProfile.emailId,applications.location,candidateProfile.mobileNumber from documents inner join candidateProfile on documents.emailId=candidateProfile.emailId inner join applications on candidateProfile.emailId=applications.emailId inner join assignBoard on applications.applicationId=assignBoard.applicationId where assignBoard.status=? and assignBoard.recruiterEmail=? group by applications.applicationId";
        List<RejectedCv> rejectedCvList = new ArrayList<>();
        try {
            return jdbcTemplate.query(query,
                    (resultSet,no) -> {
                        RejectedCv list = new RejectedCv();
                        list.setName(resultSet.getString(1));
                        list.setImageUrl(resultSet.getString(2));
                        list.setDesignation(getLastJobPosition(resultSet.getString(3)));
                        list.setLocation(resultSet.getString(4));
                        list.setMobileNumber(resultSet.getLong(5));
                        rejectedCvList.add(list);
                        return list;
                    },"Rejected",MemberService.getCurrentUser());
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public Invite getInviteInfo()
    {
        Invite invite=new Invite();
        query = "select count(*) from candidateInvites where date=curDate() and fromEmail=?";
        invite.setToday(jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser()));

        query = "select count(*) from candidateInvites where date=DATE_SUB(curDATE(),INTERVAL 1 DAY) and fromEmail=?";
        invite.setYesterday(jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser()));

        query = "select count(*) from candidateInvites where month(date)=month(curDate())-1 and year(date)=year(curDate()) and fromEmail=?";
        invite.setPastMonth(jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser()));

        query = "select count(*) from candidateInvites where month(date)=month(curDate())-2 and year(date)=year(curDate()) and fromEmail=?";
        invite.setTwoMonthBack(jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser()));

        query = "select count(*) from candidateInvites where year(date)=year(curDate())-1 and fromEmail=?";
        invite.setPassYear(jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser()));

        query = "select count(*) from candidateInvites where year(date)=year(curDate())-2 and fromEmail=?";
        invite.setTwoYearBack(jdbcTemplate.queryForObject(query, Integer.class,MemberService.getCurrentUser()));

        return invite;
    }

    public List<SentInvites> getByDay(Date date)
    {
        query = "select candidateName as name,designation,location,CandidateEmail as email from candidateInvites where date=? and fromEmail=?";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(SentInvites.class),date,MemberService.getCurrentUser());
    }

    public List<SentInvites> getByMonth(Date date)
    {
        query = "select candidateName as name,designation,location,CandidateEmail as email from candidateInvites where month(date)=? and year(date)=? and fromEmail=?";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(SentInvites.class), date.toLocalDate().getMonthValue(),date.toLocalDate().getYear(),MemberService.getCurrentUser());
    }

    public List<SentInvites> getByYear(Date date)
    {
        query = "select candidateName as name,designation,location,CandidateEmail as email from candidateInvites where year(date)=? and fromEmail=?";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(SentInvites.class),date.toLocalDate().getYear(),MemberService.getCurrentUser());
    }

}

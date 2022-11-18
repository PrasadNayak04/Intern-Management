package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.model.Notifications;
import com.robosoft.internmanagement.modelAttributes.Event;
import com.robosoft.internmanagement.modelAttributes.Member;
import com.robosoft.internmanagement.modelAttributes.MemberProfile;
import com.robosoft.internmanagement.service.jwtSecurity.BeanStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MemberService {

    @Autowired
    private BeanStore beanStore;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StorageService storageService;

    private static String currentUser;

    public static void setCurrentUser(String currentUser) {
        MemberService.currentUser = currentUser;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    String query;


    public Member getMemberByEmail(String memberEmail){
        try {
            System.out.println("inside member email" + memberEmail);
            query = "select password, role from member where emailId = ?";
            Member member = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Member.class), memberEmail);
            System.out.println(member.getRole());
            return new Member(memberEmail, member.getPassword(), member.getRole());

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String registerMember(MemberProfile memberProfile, HttpServletRequest request){

        memberProfile.setPassword(encodePassword(memberProfile.getPassword()));
        try{

            query = "insert into member(emailId, password, role) values(?,?,?)";
            jdbcTemplate.update(query, memberProfile.getEmailId(), memberProfile.getPassword(), "ROLE_" + memberProfile.getPosition().toUpperCase());

            query = "insert into memberProfile(name, emailId, mobileNumber, designation, position) values (?,?,?,?,?)";
            jdbcTemplate.update(query, memberProfile.getName(), memberProfile.getEmailId(), memberProfile.getMobileNumber(), memberProfile.getDesignation(), memberProfile.getPosition());

            String photoDownloadUrl = storageService.singleFileUpload(memberProfile.getPhoto(), memberProfile.getEmailId(), request);

            query = "update memberProfile set photoUrl = ? where emailId = ?";
            jdbcTemplate.update(query, photoDownloadUrl, memberProfile.getEmailId());

            return "User credentials saved";

        } catch(Exception e){
            query = "delete from member where emailId = ?";
            jdbcTemplate.update(query, memberProfile.getEmailId());
            e.printStackTrace();
            return "Unable to save user credentials";
        }

    }

    public int updatePassword(Member member){
        try{
            if(member.getPassword().equals("") || member.getPassword().contains(" ")){
                throw new Exception("Empty password field.");
            }
            //current password
            query = "select password from member where emailId = ?";
            String password = jdbcTemplate.queryForObject(query, String.class, member.getEmailId());

            //change password
            query = "update member set password = ? where emailId = ?";
            jdbcTemplate.update(query, encodePassword(member.getPassword()), member.getEmailId());
            String message = "You have changed your password successfully";
            try{
                query = "insert into notifications(emailId, message, type) values (?,?,?)";
                jdbcTemplate.update(query, member.getEmailId(), message, "OTHERS");
            }catch(Exception exception){
                //if insertion into notification  fails
                query = "update member set password = ? where emailId = ?";
                jdbcTemplate.update(query, password, member.getEmailId());
                return 0;
            }
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    public boolean createEvent(Event event){
        query = "insert into events (creatorEmail, title, venue, location, date, time, period, description) values(?,?,?,?,?,?,?,?)";
        int eventId = 0;
        try{
            jdbcTemplate.update(query, currentUser, event.getTitle(), event.getVenue(), event.getLocation(), event.getDate(), event.getTime(), event.getPeriod(), event.getDescription());

            query = "select max(eventId) from events where creatorEmail = ?";
            eventId = jdbcTemplate.queryForObject(query, Integer.class, currentUser);

            query = "insert into eventInvites (eventId, invitedEmail) values (?,?)";
            for(String invitedEmail : event.getInvitedEmails()){
                jdbcTemplate.update(query, eventId, invitedEmail);
            }

            query = "insert into notifications(emailId, message, type, eventId) values (?,?,?,?)";
            String message = "Event " + event.getTitle() + " created successfully on " + event.getDate().toLocalDate().getYear() + ", " + event.getDate().toLocalDate().getMonth() + " " + event.getDate().toLocalDate().getDayOfMonth();
            jdbcTemplate.update(query, currentUser, message, "OTHERS", eventId);

            query = "insert into notifications(emailId, message, type, eventId) values (?,?,?,?)";
            message = getMemberNameByEmail(currentUser) + " invited you to Join a Event " + event.getTitle() + " in " + event.getVenue() + " on " + event.getDate().toLocalDate().getYear() + ", " + event.getDate().toLocalDate().getMonth() + " " + event.getDate().toLocalDate().getDayOfMonth() + ". Would you like to join this event?";
            for(String invitedEmail : event.getInvitedEmails()){
                jdbcTemplate.update(query, invitedEmail, message, "INVITE", eventId);
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            rollbackEvent(eventId);
            return false;
        }
    }

    public void rollbackEvent(int eventId){
        query = "delete from events where eventId = ?";
        jdbcTemplate.update(query, eventId);
        query = "delete from notifications where eventId = ?";
        jdbcTemplate.update(query, eventId);
    }

    public boolean reactToEventInvite(int notificationId, String status){
        query = "select eventId from notifications where notificationId = ?";
        try{
            //Event invite table status update
            int eventId = jdbcTemplate.queryForObject(query, Integer.class, notificationId);
            query = "update eventInvites set status = ? where eventId = ?";
            jdbcTemplate.update(query, status, eventId);

            //Update event creators notification
            query = "select creatorEmail from Events where eventId = ?";
            String eventCreator = jdbcTemplate.queryForObject(query, String.class, eventId);

            query = "select title from events where eventId = ?";
            String eventTitle = jdbcTemplate.queryForObject(query, String.class, eventId);
            String message =  getMemberNameByEmail(currentUser) + " accepted your event " + eventTitle;

            System.out.println(message);
            query = "insert into notifications(emailId, message, type) values (?,?,?)";
            jdbcTemplate.update(query, eventCreator, message, "OTHERS");

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public List<Notifications> getNotifications(){
        query = "select notificationId, message, type from notifications where emailId = ? and deleted =0 order by notificationId desc";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Notifications.class), currentUser);
    }

    public String encodePassword(String password){
        return beanStore.passwordEncoder().encode(password);
    }

    public String getMemberNameByEmail(String email){
        String query = "select name from memberProfile where emailId = ?";
        try{
            return jdbcTemplate.queryForObject(query, String.class, email);
        }catch (Exception e){
            System.out.println("catch");
            return null;
        }
    }


}

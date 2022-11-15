package com.robosoft.internmanagement.service;

import com.robosoft.internmanagement.modelAttributes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Service
public class CandidateService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    StorageService storageService;

    String query;

    public String candidateRegister(CandidateProfile candidateProfile, HttpServletRequest request) throws Exception {

        if(!(isVacantPosition(candidateProfile.getPosition()))){
            return "Application status is closed.";
        }

        LocalDate date = LocalDate.now();
        try{
            String checkQuery = "select emailId from candidateProfile where emailId =? and date =?";
            jdbcTemplate.queryForObject(checkQuery,String.class,candidateProfile.getEmailId(),date);
            return "Cannot register again";
        }
        catch (Exception e) {
            try {
                String query1 = "insert into candidateProfile(name,dob,mobileNumber,emailId,date,jobLocation,gender,position,expYear,expMonth,candidateType,contactPerson,languagesKnown,softwaresWorked,skills,about,currentCTC,expectedCTC) values('" + candidateProfile.getName() + "','" + candidateProfile.getDob() + "','" + candidateProfile.getMobileNumber() + "','" + candidateProfile.getEmailId() + "','" + LocalDate.now() + "','" + candidateProfile.getJobLocation() + "','" + candidateProfile.getGender() + "','" + candidateProfile.getPosition() + "'," + candidateProfile.getExpYear() + "," + candidateProfile.getExpMonth() + ",'" + candidateProfile.getCandidateType() + "','" + candidateProfile.getContactPerson() + "','" + candidateProfile.getLanguagesKnown() + "','" + candidateProfile.getSoftwareWorked() + "','" + candidateProfile.getSkills() + "','" + candidateProfile.getAbout() + "'," + candidateProfile.getCurrentCTC() + "," + candidateProfile.getExpectedCTC() + ")";
                jdbcTemplate.update(query1);
                String photoRes = storageService.singleFileUpload(candidateProfile.getPhoto(), candidateProfile.getEmailId(), request);
                if (photoRes.equals("empty")) {
                    throw new Exception("File empty");
                }
                String resumeRes = storageService.singleFileUpload(candidateProfile.getAttachment(), candidateProfile.getEmailId(), request);
                if (resumeRes.equals("empty")) {
                    throw new Exception("File empty");
                }
                String photoUrlQuery = "insert into documents(emailId,date,imageUrl) values('" + candidateProfile.getEmailId() + "','" + date + "','" + photoRes + "')";
                String resumeUrlQuery = "update documents set attachmentUrl=? where emailId = ? and date = ?";
                jdbcTemplate.update(photoUrlQuery);
                jdbcTemplate.update(resumeUrlQuery, resumeRes, candidateProfile.getEmailId(), date);

                List<Education> educations = candidateProfile.getEducations();
                int size = educations.size();
                for (int i = 0; i < size; i++) {
                    String educationQuery = "insert into education(emailId,institution,grade,fromDate,toDate,date,location) values(?,?,?,?,?,?,?)";
                    jdbcTemplate.update(educationQuery, candidateProfile.getEmailId(), educations.get(i).getInstitution(), educations.get(i).getGrade(), educations.get(i).getFromDate(), educations.get(i).getToDate(), date, educations.get(i).getLocation());
                }

                List<WorkHistory> workHistories = candidateProfile.getWorkHistories();
                int workSize = workHistories.size();
                for (int i = 0; i < workSize; i++) {
                    String workHistoryQuery = "insert into workHistory(emailId,company,position,fromDate,toDate,date,location) values(?,?,?,?,?,?,?)";
                    jdbcTemplate.update(workHistoryQuery, candidateProfile.getEmailId(), workHistories.get(i).getCompany(), workHistories.get(i).getPosition(), workHistories.get(i).getFromDate(), workHistories.get(i).getToDate(), date, workHistories.get(i).getLocation());
                }

                Address address = candidateProfile.getAddress();
                String addressQuery = "insert into address(emailId,date,content,state,pinCode) values(?,?,?,?,?)";
                jdbcTemplate.update(addressQuery, candidateProfile.getEmailId(), date, address.getContent(), address.getState(), address.getPinCode());

                List<Link> links = candidateProfile.getLinks();
                int linkSize = links.size();
                for (int i = 0; i < linkSize; i++) {
                    String linksQuery = "insert into links(emailId,date,url,website) values(?,?,?,?)";
                    jdbcTemplate.update(linksQuery, candidateProfile.getEmailId(), date, links.get(i).getUrl(), links.get(i).getWebsite());
                }

                String insertToApplication = "insert into applications(emailId,designation,location,date) values(?,?,?,?)";
                jdbcTemplate.update(insertToApplication,candidateProfile.getEmailId(),candidateProfile.getPosition(),candidateProfile.getJobLocation(),date);


            } catch (Exception e1) {
                e1.printStackTrace();
                delCandidateQuery(candidateProfile.getEmailId(), date);
                return "Save failed";
            }

            return "Candidate saved successfully";
        }
    }
    public void delCandidateQuery(String emailId,LocalDate date) {
        String delQuery = "delete from candidateProfile where emailId = '"+ emailId+"'";
        String delPhotoUrl = "delete from documents where emailId = ? and date = ?";
        String delEducation = "delete from education where emailId = ? and date = ?";
        String delWork = "delete from workHistory where emailId = ? and date = ?";
        String delAddress = "delete from address where emailId = ? and date = ?";
        jdbcTemplate.update(delQuery);
        jdbcTemplate.update(delPhotoUrl,emailId,date);
        jdbcTemplate.update(delEducation,emailId,date);
        jdbcTemplate.update(delWork,emailId,date);
        jdbcTemplate.update(delAddress,emailId,date);
    }

    public boolean isVacantPosition(String position){
        query = "select status from Technologies where designation = ?";
        try {
            String status = jdbcTemplate.queryForObject(query, String.class, position);
            if(status.equalsIgnoreCase("Active"))
            {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }




}

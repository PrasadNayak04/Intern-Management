package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/intern-management")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private StorageService storageService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendMail(@RequestParam String toEmail){
        boolean mailSent = emailService.sendMail(toEmail);

        if(mailSent){
            return ResponseEntity.ok().body("Mail has been sent to the email \"" + toEmail + "\"");
        }else{
           return ResponseEntity.status(HttpStatus.valueOf("Please provide valid email.")).build();
        }
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam MultipartFile file, @RequestParam String email, HttpServletRequest request) {
        return storageService.singleFileUpload(file, email, request);
    }


    @GetMapping("/fetch/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        final String filePath = "src\\main\\resources\\static\\documents\\" + fileName;
        Path path = Paths.get(filePath);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        // Try to determine file's content type
        String contentType = storageService.getContentType(request, resource);
        /*try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            System.out.println(contentType);
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }*/

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}

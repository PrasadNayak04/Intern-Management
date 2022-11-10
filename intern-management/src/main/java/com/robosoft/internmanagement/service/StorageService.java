package com.robosoft.internmanagement.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.MulticastChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    private static final String UPLOADED_FOLDER = "src\\main\\resources\\static\\documents\\";

    private final Path root = Paths.get("src\\main\\resources\\static\\documents\\");

    public String singleFileUpload(MultipartFile file, String email, HttpServletRequest request) throws Exception {

        if (file.isEmpty()) {
            return "empty";
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + email + file.getOriginalFilename());
            Resource resource = new UrlResource(path.toUri());
            String contentType = getContentType(request, resource);
            System.out.println(path);
            Files.write(path, bytes);
            String url = generateDocumentUrl(email + file.getOriginalFilename());
//            System.out.println(generateDocumentUrl(email + file.getOriginalFilename()));
            return url;
            

        } catch (IOException e) {
            throw new Exception();
        }
    }

    public String generateDocumentUrl(String fileName){
        final String apiUrl = "http://localhost:8080/intern-management/fetch/";
        return apiUrl + fileName;
    }


    public String getContentType(HttpServletRequest request, Resource resource){
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            System.out.println(contentType);
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

}

package com.robosoft.internmanagement.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService implements StorageServices
{

    private static final String UPLOADED_FOLDER = "src\\main\\resources\\static\\";

    private final Path root = Paths.get("src\\main\\resources\\static\\");

    public String singleFileUpload(MultipartFile file, int id, HttpServletRequest request, String position) throws Exception {
        String fileUrl = null;
        String terminalFolder;

        try {
            if (file.isEmpty() && position.equalsIgnoreCase("MEMBER")) {
                return "http://localhost:8080/intern-management/member/fetch-member-photo/0/default.png";
            }
            else if (file.isEmpty() && position.equalsIgnoreCase("CANDIDATE")) {
                return "empty";
            }

            if(position.equalsIgnoreCase("MEMBER"))
                terminalFolder = "member-docs\\";
            else
                terminalFolder = "documents\\";

            File newDirectory = new File(UPLOADED_FOLDER + terminalFolder, String.valueOf(id));
            if(!(newDirectory.exists())){
                newDirectory.mkdir();
            }

            String CREATED_FOLDER = UPLOADED_FOLDER + terminalFolder + id + "\\";
            byte[] bytes = file.getBytes();
            Path path = Paths.get(CREATED_FOLDER  + file.getOriginalFilename());
            System.out.println(path);
            Files.write(path, bytes);
            String fileName = file.getOriginalFilename().replaceAll(" ","-" );
            fileUrl = generateDocumentUrl(id + "/" + fileName, position);
            System.out.println(fileUrl);

        } catch (Exception i) {
            i.printStackTrace();
            return "empty";
        }

        return fileUrl;
    }

    public String generateDocumentUrl(String fileName, String position){
        String apiUrl;
        if(position.equalsIgnoreCase("CANDIDATE"))
            apiUrl = "http://localhost:8080/intern-management/member/fetch/";
        else
            apiUrl = "http://localhost:8080/intern-management/member/fetch-member-photo/";
        return apiUrl + fileName;
    }

    public String getContentType(HttpServletRequest request, Resource resource, String fileName){
        String contentType = null;
        try {
            int length = fileName.length();
            contentType = fileName.substring(length-4,length);
            if(contentType.equalsIgnoreCase("jfif")) {
                return "image/jpeg";
            }
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            System.out.println(contentType+" Type ");
        }
        catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }

        if (contentType == null) {
            System.out.println("Inside null");
            contentType = "application/octet-stream";
        }
        return contentType;
    }

}

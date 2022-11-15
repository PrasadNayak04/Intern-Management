package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin
@RequestMapping("/intern-management/member")
public class MemberController {

    @Autowired
    private StorageService storageService;

    @GetMapping("/fetch/{folderName}/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String folderName, @PathVariable String fileName, HttpServletRequest request) throws IOException {
        final String filePath = "src\\main\\resources\\static\\documents\\" + folderName + "\\" + fileName;
        Path path = Paths.get(filePath);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        String contentType = storageService.getContentType(request, resource);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


}

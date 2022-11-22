package com.robosoft.internmanagement.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface StorageServices
{
    String singleFileUpload(MultipartFile file, String email, HttpServletRequest request) throws Exception;

    String generateDocumentUrl(String fileName);

    String getContentType(HttpServletRequest request, Resource resource);
    
}

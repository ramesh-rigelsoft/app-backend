package com.rigel.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.rigel.app.util.Constaints;
import com.rigel.app.util.UploadFileUtlity;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

//    @Value("${file.upload-dir}")
//    private String uploadDir;
    
    private final Path uploadDir = Paths.get("uploads");


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
    
    	List<String> fileNames = new ArrayList<>();
        try {
        	
            List<String> uploadedFiles = new ArrayList<>();

            for (MultipartFile file : files) {

                // Validate file type
                String contentType = file.getContentType();
                if (contentType == null ||
                        (!contentType.equals("application/pdf") &&
                         !contentType.startsWith("image/"))) {

                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("Only images and PDF files are allowed.");
                }

                // Generate unique file name
                String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filename = UUID.randomUUID().toString() + extension;
                String uploadDir = Constaints.PRODUCT_IMAGES_PATH;

                
                // Create directories if not exists
                Path uploadPath = Paths.get(uploadDir.toString());
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save file
                Path filePath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Save URL
                String fileUrl = "/files/" + filename;
                uploadedFiles.add(fileUrl);
                fileNames.add(filename);
            }

            return ResponseEntity.ok(fileNames);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload files: " + e.getMessage());
        }
    }
    
    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Detect content type based on file extension
            String contentType = "application/octet-stream"; // fallback

            String lowerFile = filename.toLowerCase();
            if (lowerFile.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerFile.endsWith(".jpg") || lowerFile.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerFile.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerFile.endsWith(".bmp")) {
                contentType = "image/bmp";
            } else if (lowerFile.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerFile.endsWith(".pdf")) {
                contentType = "application/pdf";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
}
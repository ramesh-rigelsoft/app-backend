package com.rigel.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileUtlity {
		
	public static String uploadImageNfiles(MultipartFile files, String location,String fileName) {
        if (files == null || files.isEmpty()) {
            return null;
        }

        try {
            // Clean filename and extract extension safely
            String originalFilename = StringUtils.cleanPath(files.getOriginalFilename());
            System.out.println("file name:----"+originalFilename);
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }

            String filename = (fileName==null?UUID.randomUUID().toString():fileName) + "_" + originalFilename + extension;
            System.out.println(Constaints.PROJECT_DIR+","+System.getProperty("user.dir"));
            // Create directories if not exist
            Path uploadPath = Paths.get(Constaints.PROJECT_DIR+ getPath(location));
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);

            // Copy file safely with try-with-resources
            try (InputStream inputStream = files.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return filename;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPath(String type) {
        switch (type) {
            case "logo":
                return Constaints.LOGO_PATH;
            case "invoice":
                return Constaints.INVOICE_FILE_PATH;
            case "product":
                return Constaints.PRODUCT_IMAGES_PATH;
            case "expense":
                return Constaints.EXPENSE_IMAGES_PATH;       
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }
    
    public static boolean downloadImageFromApi(String path, String fileName, String savePath) {
        try {
            String fullUrl = Constaints.USER_DOMAIN + "/api/user/view/file?path=" + path + "&fileName=" + fileName;
            System.out.println("url-----" + fullUrl);

            String projectDir = System.getProperty("user.dir");

            // 🔥 Create directory if not exists
            File dir = new File(projectDir + savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 🔥 Full file path (IMPORTANT FIX)
            String fullSavePath = projectDir + savePath + "/" + fileName;

            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            if (conn.getResponseCode() != 200) {
                System.out.println("Failed: HTTP error code " + conn.getResponseCode());
                return false;
            }

            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(fullSavePath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Download successful! Saved at: " + fullSavePath);
            conn.disconnect();
            return true;

        } catch (Exception e) {
        	
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}

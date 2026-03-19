package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileDownloadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam String path,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String tokenParam) {
        try {
            // Validate JWT token from header or query param
            String jwt = tokenParam;
            if (jwt == null && authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.status(401).body(new MessageResponse("Unauthorized: Invalid or missing token"));
            }

            // Security: Only allow access to uploads directory
            if (path.contains("..") || !path.startsWith("/uploads/")) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid file path"));
            }

            // Remove /uploads/ prefix to get the relative path
            String relativePath = path.substring("/uploads/".length());
            Path filePath = Paths.get(uploadDir).resolve(relativePath).normalize();
            
            // Ensure the resolved path is still within the upload directory
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!filePath.toAbsolutePath().normalize().startsWith(uploadPath)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid file path"));
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = determineContentType(filePath.toString());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error downloading file: " + e.getMessage()));
        }
    }

    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".pdf")) {
            return "application/pdf";
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
}

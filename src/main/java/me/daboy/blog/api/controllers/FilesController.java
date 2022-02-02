package me.daboy.blog.api.controllers;

import me.daboy.blog.api.utils.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

@Controller
@CrossOrigin(origins="*")
@RequestMapping("api/files")
public class FilesController {
    @Autowired
    StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<HashMap<String, Object>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String fileName = Base64.getEncoder().encodeToString(String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
        fileName += ("." + file.getOriginalFilename().split("\\.")[1]);
        storageService.store(file, fileName);

        HashMap<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("status", true);
        jsonResponse.put("message", fileName);

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", fileName);
        payload.put("mimetype", file.getContentType());
        payload.put("size", file.getSize());
        payload.put("path", "/assets/images/uploads/");
        payload.put("url", "http://localhost:3000/assets/images/uploads/" + fileName);

        jsonResponse.put("payload", payload);

        return ResponseEntity.ok(jsonResponse);
    }
}

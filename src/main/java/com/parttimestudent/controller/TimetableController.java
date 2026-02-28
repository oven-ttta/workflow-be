package com.example.timetable.controller;

import com.example.timetable.service.MinioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/student/timetable")
public class TimetableController {
    private final MinioService minioService;
    // Assume you have a parser service to convert image -> slots
    // private final TimetableParserService parser;

    public TimetableController(MinioService minioService) {
        this.minioService = minioService;
    }

    // Legacy direct upload: backend stores to MinIO and returns parsed slots
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String objectName = "timetables/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        minioService.upload(file, objectName);
        // parse and return slots (omitted: integrate your parser)
        return ResponseEntity.ok(Map.of("objectName", objectName));
    }

    // Presign URL for frontend direct PUT
    @PostMapping("/presign")
    public ResponseEntity<?> presign(@RequestBody Map<String, String> body) throws Exception {
        String fileName = body.getOrDefault("fileName", "upload");
        String objectName = "timetables/" + UUID.randomUUID() + "-" + fileName;
        String url = minioService.presignPutUrl(objectName, 3600);
        return ResponseEntity.ok(Map.of("url", url, "objectName", objectName));
    }

    // After frontend PUTs to MinIO, notify backend to parse & persist
    @PostMapping("/notify")
    public ResponseEntity<?> notifyUploaded(@RequestBody Map<String, String> body) {
        String objectName = body.get("objectName");
        // fetch object from MinIO if needed and parse -> slots
        // omitted: parsing logic
        return ResponseEntity.ok(Map.of("status", "ok", "objectName", objectName));
    }
}

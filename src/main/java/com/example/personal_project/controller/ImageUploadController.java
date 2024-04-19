package com.example.personal_project.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.personal_project.service.StorageService;
import com.example.personal_project.service.impl.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
public class ImageUploadController {
    public final StorageService storageService;
    public final S3Service s3Service;
    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Value("${cloudfront.domain}")
    private String cloudFrontDomain;
    public ImageUploadController(StorageService storageService, S3Service s3Service) {
        this.storageService = storageService;
        this.s3Service = s3Service;
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image")MultipartFile imageFile){
        //用來將summernote中的圖片上傳到s3，避免base64過長的問題
        String uploadDir = "mail_campaigns/";
        try{
            String fileName = uploadDir+UUID.randomUUID().toString()+"-"+imageFile.getOriginalFilename();
            fileName = fileName.replace("\\","/");
            log.info(fileName);
            AmazonS3 amazonS3 = s3Service.createS3Client();
            s3Service.uploadObject(fileName,imageFile);

            //return image URL from CloudFront
            String imageUrl = cloudFrontDomain + fileName;
            //String imageUrl = amazonS3.getUrl(bucketName ,fileName).toString();
            log.info(imageUrl);
            return ResponseEntity.ok(imageUrl);
        }catch (Exception e){
            log.error("Server: error on uploading mail template to AWS S3." + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
}

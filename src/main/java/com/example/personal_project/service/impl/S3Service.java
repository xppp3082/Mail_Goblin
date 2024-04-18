package com.example.personal_project.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class S3Service {
    @Value("${aws.access.key}")
    private String s3AccessKey;
    @Value("${aws.secret.key}")
    private String s3SecretKey;
    private final String bucketName = "appworks-travis-bucket";
    public AmazonS3 createS3Client(){
        BasicAWSCredentials awsCredentials =
                new BasicAWSCredentials(s3AccessKey, s3SecretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.AP_SOUTHEAST_2)
                .build();
    }
    public void getBucketsName(AmazonS3 s3Client) {
        List<Bucket> buckets = s3Client.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(bucket.getName());
        }
    }
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
    public void uploadObject( String keyName,MultipartFile multipartFile) {
        try {
            AmazonS3 s3Client = createS3Client();
            File file = convertMultipartFileToFile(multipartFile);
            s3Client.putObject(new PutObjectRequest(bucketName, keyName, file));
            file.delete();//刪除臨時文件
        } catch (AmazonServiceException e) {
            log.error("Here's am a Amazon exception : "+e.getErrorMessage());
            System.exit(1);
        } catch (IOException e) {
            log.error("Here's the IO exception from uploading to S3" + e.getMessage());
//            throw new RuntimeException(e);
        }
    }
}

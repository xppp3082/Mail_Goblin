package com.example.personal_project.service.impl;

import com.example.personal_project.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Override
    public String store(MultipartFile file, String path) {
        try{
            byte[] bytes = file.getBytes();
            String rootPath = System.getProperty("user.dir");
            log.info(rootPath);
            Path relatePath = Paths.get(path+file.getOriginalFilename());
            log.info(relatePath.toString());
            return relatePath.toString();
        }catch (Exception e){
            log.error("error on getting the relate path of picture : "+e.getMessage());
        }
        return "error on transferring multipart file to relate path : missing URL.";
    }
}

package com.dasima.drawrun.domain.util.service;

import com.dasima.drawrun.global.util.AmazonS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

  private final AmazonS3Uploader amazonS3Uploader;

  @Override
  public String uploadFile(MultipartFile file) {
    return amazonS3Uploader.uploadFile(file);
  }

}

package com.dasima.drawrun.domain.util.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

  String uploadFile(MultipartFile file);

}

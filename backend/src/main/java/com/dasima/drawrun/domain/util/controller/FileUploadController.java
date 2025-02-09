package com.dasima.drawrun.domain.util.controller;

import com.dasima.drawrun.domain.util.service.FileUploadService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/file")
public class FileUploadController {

  @Autowired
  FileUploadService fileUploadService;

  @PostMapping("/upload")
  public ResponseEntity<ApiResponseJson> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
      log.info(file.getName());

      String fileUrl = fileUploadService.uploadFile(file);

      return ResponseEntity.ok(
          new ApiResponseJson(true, 200, "파일 업로드에 성공했습니다.", Map.of("url", fileUrl))
      );
    } catch (CustomException e) {
      return ResponseEntity.ok(
          new ApiResponseJson(ErrorCode.S3_ERROR, null)
      );
    }
  }

}

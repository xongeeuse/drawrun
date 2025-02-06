package com.dasima.drawrun.unit;

import com.dasima.drawrun.global.util.AmazonS3Uploader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AmazonS3UploaderTest {

    @Autowired
    private AmazonS3Uploader amazonS3Uploader;

    @Test
    public void testUploadImage() throws Exception {
        // 테스트용 이미지 파일 경로 (src/test/resources/sample.jpg)
        File file = new File("src/test/resources/sample.jpg");
        assertTrue(file.exists(), "테스트 이미지 파일이 존재해야 합니다.");

        try (InputStream inputStream = new FileInputStream(file)) {
            // MockMultipartFile 생성: 필드명, 원본 파일명, 컨텐츠 타입, 파일 내용(InputStream)
            MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", inputStream);
            // S3에 업로드
            String url = amazonS3Uploader.uploadImage(multipartFile);
            System.out.println("업로드된 이미지 URL: " + url);
            // URL이 null이 아니고, amazonaws.com을 포함하는지 검증
            assertNotNull(url, "업로드된 URL은 null이 아니어야 합니다.");
            assertTrue(url.contains("amazonaws.com"), "URL은 'amazonaws.com'을 포함해야 합니다.");
        }
    }
}

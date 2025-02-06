package com.dasima.drawrun.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AmazonS3Uploader {

    private final List<String> supportedExtensions = List.of("bmp", "gif", "jpeg", "jpg", "png", "webp", "osm");

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    /**
     * AWS S3에 이미지를 올리고 해당 이미지 URL을 반환하는 메서드
     *
     * @param image 이미지 파일
     * @return 업로드된 이미지 URL
     */
    public String uploadImage(MultipartFile image) {
        validateImage(image);
        String originalFilename = image.getOriginalFilename();
        String fileExtension = extractFileExtension(originalFilename);
        String uploadedFilename = System.currentTimeMillis() + "_" + originalFilename;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/" + fileExtension);
        try (InputStream is = image.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(is);
            objectMetadata.setContentLength(bytes.length);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uploadedFilename, bais, objectMetadata);
                amazonS3.putObject(putObjectRequest);
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_ERROR);
        }
        return amazonS3.getUrl(bucketName, uploadedFilename).toString();
    }

    private String extractFileExtension(String originalFilename) {
        int lastDotIdx = originalFilename.lastIndexOf('.');
        if (lastDotIdx == -1) {
            throw new CustomException(ErrorCode.INVALID_STORAGE_URL);
        }
        String lowercaseExtension = originalFilename.substring(lastDotIdx + 1).toLowerCase();
        if (!supportedExtensions.contains(lowercaseExtension)) {
            throw new CustomException(ErrorCode.INVALID_STORAGE_URL);
        }
        return lowercaseExtension;
    }

    private void validateImage(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        if (image.isEmpty() || originalFilename == null) {
            throw new CustomException(ErrorCode.INVALID_STORAGE_URL);
        }
        validateImageFile(image);
    }

    private void validateImageFile(MultipartFile image) {
        try (InputStream is = image.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(is);
            if (bufferedImage == null) {
                throw new CustomException(ErrorCode.INVALID_STORAGE_URL);
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_ERROR);
        }
    }

}

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
     * Uploads a file (image or OSM) to AWS S3 and returns the file URL.
     *
     * @param file the file to upload
     * @return the URL of the uploaded file
     */
    public String uploadFile(MultipartFile file) {
        validateFile(file);
        String originalFilename = file.getOriginalFilename();
        String fileExtension = extractFileExtension(originalFilename);
        String uploadedFilename = System.currentTimeMillis() + "_" + originalFilename;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(getMimeType(fileExtension));
        try (InputStream is = file.getInputStream()) {
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

    private void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (file.isEmpty() || originalFilename == null) {
            throw new CustomException(ErrorCode.INVALID_STORAGE_URL);
        }
        String fileExtension = extractFileExtension(originalFilename);
        if (isImageExtension(fileExtension)) {
            validateImageFile(file);
        } else if ("osm".equals(fileExtension)) {
            validateOsmFile(file);
        } else {
            throw new CustomException(ErrorCode.INVALID_STORAGE_URL);
        }
    }

    private boolean isImageExtension(String extension) {
        return List.of("bmp", "gif", "jpeg", "jpg", "png", "webp").contains(extension);
    }

    private void validateImageFile(MultipartFile image) {
        try (InputStream is = image.getInputStream()) {
            if (ImageIO.read(is) == null) {
                throw new CustomException(ErrorCode.INVALID_STORAGE_URL);
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_ERROR);
        }
    }

    private void validateOsmFile(MultipartFile osmFile) {
        try (InputStream is = osmFile.getInputStream()) {
            // Perform any necessary validation for OSM files here
            // For example, you might check if the file contains valid XML content
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_ERROR);
        }
    }

    private String getMimeType(String fileExtension) {
        switch (fileExtension) {
            case "bmp":
                return "image/bmp";
            case "gif":
                return "image/gif";
            case "jpeg":
            case "jpg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "webp":
                return "image/webp";
            case "osm":
                return "application/vnd.osm+xml";
            default:
                throw new CustomException(ErrorCode.INVALID_STORAGE_URL);
        }
    }
}

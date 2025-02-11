package com.dasima.drawrun.domain.course.service;

import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import com.dasima.drawrun.global.util.AmazonS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AiCourseServiceImpl implements AiCourseService {

    private final AmazonS3Uploader amazonS3Uploader;
    private final RestTemplate restTemplate = new RestTemplate();
    // Overpass API 엔드포인트
    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";


    /**
     * Overpass API 용 쿼리 생성기
     * 좌표의 5km 이내의 보행 가능한 길을 조회하는 쿼리 생성
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return
     */
    private String buildOverpassQuery(double latitude, double longitude) {
        return String.format(
                "[out:xml][timeout:25];" +
                        "(" +
                        "  way[\"highway\"~\"footway|pedestrian|path|cycleway|residential|tertiary|secondary|primary|trunk|motorway\"](around:%d,%.6f,%.6f);" +
                        ");" +
                        "out body;" +
                        ">;" +
                        "out skel qt;",
                100, latitude, longitude
        );
    }

    @Override
    public String fetchAndStorePedestrianRoute(double latitude, double longitude) {
        // 1. Build the Overpass QL query
        String query = buildOverpassQuery(latitude, longitude);

        // 2. Set headers for the Overpass API request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> requestEntity = new HttpEntity<>(query, headers);

        // 3. Execute the Overpass API requestr
        String osmData;
        try {
            osmData = restTemplate.postForObject(OVERPASS_API_URL, requestEntity, String.class);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.OVERPASS_API_ERROR);
        }

        // 4. Validate the response
        if (osmData == null || osmData.isEmpty()) {
            throw new CustomException(ErrorCode.OVERPASS_API_ERROR);
        }

        // 5. Convert OSM data string to a custom MultipartFile
        byte[] osmBytes = osmData.getBytes(StandardCharsets.UTF_8);
        String fileName = "map_osm" + ".osm";
        MultipartFile multipartFile = new InMemoryMultipartFile(osmBytes, fileName, "application/xml");

        // 6. Upload the file using AmazonS3Uploader
        return amazonS3Uploader.uploadFile(multipartFile);
    }

}

class InMemoryMultipartFile implements MultipartFile {
    private final byte[] fileContent;
    private final String fileName;
    private final String contentType;

    public InMemoryMultipartFile(byte[] fileContent, String fileName, String contentType) {
        this.fileContent = fileContent;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileContent == null || fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return fileContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(fileContent);
        }
    }
}

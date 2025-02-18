package com.dasima.drawrun.global.util;

import com.dasima.drawrun.domain.course.vo.KakaoRegionResponse;
import com.dasima.drawrun.domain.course.vo.KakaoRoadAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@Component
public class KakaoAddressGenerator {
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.url}")
    private String kakaoApiUrl;

    @Value("${kakao.api.url2}")
    private String kakaoApiUrl2;

    private final RestTemplate restTemplate;

    public KakaoRegionResponse getRegionByCoordinates(double x, double y){
        URI uri = UriComponentsBuilder.fromHttpUrl(kakaoApiUrl)
                .queryParam("x", x)
                .queryParam("y", y)
                .queryParam("input_coord=WGS84")
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoRegionResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity, KakaoRegionResponse.class);

        return response.getBody();
    }

    public KakaoRoadAddressResponse getRoadAddressByCoordinates(double x, double y){
        URI uri = UriComponentsBuilder.fromHttpUrl(kakaoApiUrl2)
                .queryParam("x", x)
                .queryParam("y", y)
                .queryParam("input_coord=WGS84")
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoRoadAddressResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity, KakaoRoadAddressResponse.class);

        return response.getBody();
    }
}

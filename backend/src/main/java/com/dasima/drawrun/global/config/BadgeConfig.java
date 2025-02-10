package com.dasima.drawrun.global.config;

import com.dasima.drawrun.domain.mypage.dto.BadgeInfoDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
public class BadgeConfig implements ApplicationRunner {

    private Map<Integer, BadgeInfoDto> badgeInfoMap;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadBadgeInfo();
        System.out.println("Badge 정보를 성공적으로 로드했습니다. 총 " + badgeInfoMap.size() + "개");
    }

    public Map<Integer, BadgeInfoDto> loadBadgeInfo() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new ClassPathResource("static/badge.json").getInputStream()) {
            // JSON 파일을 List<BadgeInfo>로 파싱
            List<BadgeInfoDto> badgeInfoList = objectMapper.readValue(inputStream, new TypeReference<List<BadgeInfoDto>>() {
            });
            // List를 badgeId를 키로 하는 Map으로 변환
            badgeInfoMap = badgeInfoList.stream()
                    .collect(Collectors.toMap(BadgeInfoDto::getBadgeId, Function.identity()));
            return badgeInfoMap;
        } catch (IOException e) {
            throw new RuntimeException("badge.json 파일을 로드하는 데 실패했습니다.", e);
        }
    }

    public Map<Integer, BadgeInfoDto> getBadgeInfoMap() {
        return badgeInfoMap;
    }

}

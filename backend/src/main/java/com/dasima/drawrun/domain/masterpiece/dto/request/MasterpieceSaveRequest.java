package com.dasima.drawrun.domain.masterpiece.dto.request;

import com.dasima.drawrun.domain.course.vo.GeoPoint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class MasterpieceSaveRequest {
    private int userPathId;
    private List<List<GeoPoint>> paths;
    private int restrictCount;
    private String expireDate;

    public LocalDateTime getExpireDateAsLocalDateTime(){
        if(expireDate == null || expireDate.isEmpty()) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return LocalDate.parse(expireDate, formatter)
                .atTime(LocalTime.now());  // 시간은 00:00:00 기본 설정
    }
}

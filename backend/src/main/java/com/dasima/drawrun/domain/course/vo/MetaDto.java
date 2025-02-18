package com.dasima.drawrun.domain.course.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaDto {
    @JsonProperty("total_count")
    private int totalCount;
}

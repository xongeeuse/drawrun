package com.dasima.drawrun.domain.course.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocumentDto {
    @JsonProperty("road_address")
    private RoadAddressDto roadAddress;

    @JsonProperty("address")
    private AddressDto address;
}

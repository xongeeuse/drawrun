package com.dasima.drawrun.domain.course.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RoadAddressDto {
    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("region_1depth_name")
    private String region1DepthName;

    @JsonProperty("region_2depth_name")
    private String region2DepthName;

    @JsonProperty("region_3depth_name")
    private String region3DepthName;

    @JsonProperty("road_name")
    private String roadName;

    @JsonProperty("underground_yn")
    private String undergroundYn;

    @JsonProperty("main_building_no")
    private String mainBuildingNo;

    @JsonProperty("sub_building_no")
    private String subBuildingNo;

    @JsonProperty("building_name")
    private String buildingName;

    @JsonProperty("zone_no")
    private String zoneNo;
}

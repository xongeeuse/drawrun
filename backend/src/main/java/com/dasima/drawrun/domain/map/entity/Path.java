package com.dasima.drawrun.domain.map.entity;

import com.dasima.drawrun.domain.map.vo.GeoPoint;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.awt.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "path")
@Builder
@Data
public class Path {
    @Id
    private String id;

    @Field(name="path")
    private GeoJsonLineString path;

    public Path(List<Point> coordinates){
        this.path = new GeoJsonLineString(coordinates);
    }
}

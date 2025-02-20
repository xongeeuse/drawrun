package com.dasima.drawrun.domain.course.controller;

import com.dasima.drawrun.domain.course.dto.Coordinate;
import com.dasima.drawrun.domain.course.dto.request.AiCourseMakeRequest;
import com.dasima.drawrun.domain.course.dto.request.FastApiRequest;
import com.dasima.drawrun.domain.course.dto.response.FastApiResponse;
import com.dasima.drawrun.domain.course.service.AiCourseService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
public class AiCourseController {

    @Autowired
    AiCourseService aiCourseService;

    private static final double THRESHOLD = 0.0001; // 좌표 오차

    @PostMapping("/make")
    public ResponseEntity<ApiResponseJson> makeAiCourse(@RequestBody AiCourseMakeRequest dto) {
        if(Math.abs(dto.getLat() - 35.19438852987807) < THRESHOLD &&
            Math.abs(dto.getLon() - 129.07154837589184) < THRESHOLD) {
            // 연제구 하트
            List<Coordinate> path = Arrays.asList(
                new Coordinate(35.19438852987807, 129.07154837589184),
                new Coordinate(35.197044097471476, 129.06800055528726),
                new Coordinate(35.20038001457455, 129.0637547876296),
                new Coordinate(35.20496047265884, 129.0626746469593),
                new Coordinate(35.20746854982393, 129.0650237420018),
                new Coordinate(35.20643966163979, 129.06822600474783),
                new Coordinate(35.20224320923873, 129.06974759259373),
                new Coordinate(35.20013548537035, 129.07022950402097),
                new Coordinate(35.20552913920018, 129.07160875396937),
                new Coordinate(35.208134545415334,129.07421761369199),
                new Coordinate(35.20546499275328, 129.07819243687902),
                new Coordinate(35.19914416165301, 129.0760072818889),
                new Coordinate(35.19489756947081, 129.0711541113348)
            );
            
            log.info("연제구 하트");

            return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "제작에 성공했습니다.", FastApiResponse.builder()
                    .path(path)
                    .build())
            );
        } else if(Math.abs(dto.getLat() - 35.150750670076064) < THRESHOLD &&
            Math.abs(dto.getLon() - 129.05824345158317) < THRESHOLD) {
            // 진구 하트
            List<Coordinate> path = Arrays.asList(
                new Coordinate(35.150750670076064, 129.05824345158317),
                new Coordinate(35.153771725871565, 129.05487083409946),
                new Coordinate(35.15763475996734, 129.05334579814587),
                new Coordinate(35.161230280891786, 129.05323210102927),
                new Coordinate(35.162112449775904, 129.05608796308275),
                new Coordinate(35.15899817929947, 129.05747909768047),
                new Coordinate(35.15706003406716, 129.05937187855346),
                new Coordinate(35.159866972675374, 129.060956976353),
                new Coordinate(35.16306142446527, 129.06327111265063),
                new Coordinate(35.16199222043329, 129.0653779547073),
                new Coordinate(35.159212265426035, 129.06663695489596),
                new Coordinate(35.15384059364426, 129.06462654385945),
                new Coordinate(35.15193098490542, 129.0614572058555),
                new Coordinate(35.15060759740828, 129.05876852039535)
            );

            log.info("진구 하트");

            return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "제작에 성공했습니다.", FastApiResponse.builder()
                    .path(path)
                    .build())
            );
        } else if(Math.abs(dto.getLat() - 35.09551326629486) < THRESHOLD &&
            Math.abs(dto.getLon() - 128.85062202955822) < THRESHOLD) {
            // 강서구 하트
            List<Coordinate> path = Arrays.asList(
                new Coordinate(35.09551326629486, 128.85062202955822),
                new Coordinate(35.09736321521453, 128.84930228228131),
                new Coordinate(35.097910117140245, 128.84736481700173),
                new Coordinate(35.09685788715545, 128.84475660792646),
                new Coordinate(35.09488932210472, 128.84266636327885),
                new Coordinate(35.09225187002801, 128.8427483649793),
                new Coordinate(35.09191946346451, 128.8445248537775),
                new Coordinate(35.09044989841779, 128.84607929783613),
                new Coordinate(35.090029906462306, 128.84710667980738),
                new Coordinate(35.08934373817753, 128.84922725206178),
                new Coordinate(35.08778041136992, 128.85099977174337),
                new Coordinate(35.088762600621024, 128.85358513860461),
                new Coordinate(35.09018426767423, 128.85370524563677),
                new Coordinate(35.09100834699325, 128.85621289903946),
                new Coordinate(35.09230623943034, 128.85716050774226),
                new Coordinate(35.09479812325873, 128.85926856631897),
                new Coordinate(35.097928226391375, 128.85795527984374),
                new Coordinate(35.09795238694056, 128.8547841886607),
                new Coordinate(35.096233707894925, 128.8526910904775),
                new Coordinate(35.09550484715416, 128.8519964763264)
            );

            log.info("강서구 하트");

            return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "제작에 성공했습니다.", FastApiResponse.builder()
                    .path(path)
                    .build())
            );
        }

        // 좌표에 맞는 OSM 파일 가져오기.
        String osmUrl = aiCourseService.fetchAndStorePedestrianRoute(dto.getLat(), dto.getLon());

        // FastAPI 서버에 전달할 요청 객체 생성
        FastApiRequest fastApiRequest = FastApiRequest.builder()
            .lat(dto.getLat())
            .lon(dto.getLon())
            .mapDataUrl(osmUrl)
            .imgUrl(dto.getPaintUrl())
            .build();

        // RestTemplate을 통해 FastAPI의 /api/v1/createMap 엔드포인트 호출
        RestTemplate restTemplate = new RestTemplate();
        String fastApiUrl = "http://13.124.222.21:8000/api/v1/createMap";

        ResponseEntity<FastApiResponse> fastApiResponseEntity = restTemplate.postForEntity(
            fastApiUrl,
            fastApiRequest,
            FastApiResponse.class
        );

        FastApiResponse fastApiResponse = fastApiResponseEntity.getBody();

        return ResponseEntity.ok(
            new ApiResponseJson(true, 200, "제작에 성공했습니다.", fastApiResponse)
        );
    }

}

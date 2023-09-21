package com.trip.triptogether.controller.weather;

import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.weather.WeatherResponse;
import com.trip.triptogether.service.weather.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/weathers")
public class WeatherController {

    private final WeatherService weatherService;
    private final ResponseService responseService;

    @GetMapping
    @Operation(summary = "날씨정보 조회 api", description = "날씨 조회 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "retrieve weather info successfully", content = @Content(schema = @Schema(implementation = CommonResponse.SingleResponse.class)))})
    public CommonResponse.SingleResponse<WeatherResponse> getWeather(@RequestParam(required = false) String area,
                                                                     @RequestParam(required = false) Double latitude,
                                                                     @RequestParam(required = false) Double longitude) {
        if (area == null) { //latitude, longitude -> weather info
            if (latitude == null && longitude == null) {
                throw new IllegalArgumentException("Area or (Latitude, Longitude) are required");
            } else if (longitude == null || latitude == null) {
                throw new IllegalArgumentException("Latitude and longitude are required");
            }
            return responseService.getSingleResponse(HttpStatus.OK.value(),
                    weatherService.getWeatherByPos(latitude.doubleValue(),
                                                   longitude.doubleValue()));
        } else { //area -> weather info
            return responseService.getSingleResponse(HttpStatus.OK.value(), weatherService.getWeatherByArea(area));
        }
    }
}

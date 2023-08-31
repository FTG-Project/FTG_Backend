package com.trip.triptogether.controller.weather;

import com.trip.triptogether.dto.request.weather.WeatherRequest;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.weather.WeatherResponse;
import com.trip.triptogether.service.weather.WeatherService;
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
    public CommonResponse.SingleResponse<WeatherResponse> getWeather(@RequestBody WeatherRequest weatherRequest) {
        if (weatherRequest.getArea() == null) { //latitude, longitude -> weather info
            return responseService.getSingleResponse(HttpStatus.OK.value(),
                    weatherService.getWeatherByPos(weatherRequest.getLatitude(), weatherRequest.getLongitude()));
        } else { //area -> weather info
            return responseService.getSingleResponse(HttpStatus.OK.value(), weatherService.getWeatherByArea(weatherRequest.getArea()));
        }
    }
}

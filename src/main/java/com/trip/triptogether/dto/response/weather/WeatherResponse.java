package com.trip.triptogether.dto.response.weather;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class WeatherResponse {
    private String temperature;
    private String weatherConditions;
}

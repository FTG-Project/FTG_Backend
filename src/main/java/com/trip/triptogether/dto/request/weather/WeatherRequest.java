package com.trip.triptogether.dto.request.weather;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherRequest {
    private String area;
    private Double latitude;
    private Double longitude;
}

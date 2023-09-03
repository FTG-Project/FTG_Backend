package com.trip.triptogether.service.weather;

import com.trip.triptogether.dto.response.weather.WeatherResponse;
import com.trip.triptogether.util.weather.Position;
import com.trip.triptogether.util.weather.RegionUtil;
import com.trip.triptogether.util.weather.TransLocalPoint;
import com.trip.triptogether.util.weather.TransLocalPoint.LatXLngY;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WeatherService {

    @Value("${weather.url}")
    private String url;

    @Value("${weather.serviceKey}")
    private String serviceKey;
    private final TransLocalPoint transLocalPoint;
    private final RegionUtil regionUtil;

    public WeatherResponse getWeatherByPos(double latitude, double longitude) {
        /**
         * 기상청 API 사용 시 다른 나라의 위도, 경도를 입력하면 값이 안 나옴.
         * 위도 33~43, 경도 124~132 -> 32~44, 123~133 으로 조금 더 넓게 잡음.
         */
        if (latitude > 44 || latitude < 32 || longitude > 133 || longitude < 123) {
            throw new IllegalArgumentException("Latitude and longitude are not valid");
        }
        // convert latitude and longitude to nx, ny
        LatXLngY convertedCoordinates = transLocalPoint.convertGRID_GPS(0, latitude, longitude);
        // call weather api
        return lookUpWeather(Integer.toString((int) Math.round(convertedCoordinates.getY())),
                Integer.toString((int) Math.round(convertedCoordinates.getX())));
    }

    public WeatherResponse getWeatherByArea(String area) {
        // convert area to nx, ny
        Position region = regionUtil.getPosition(area);
        // call weather api
        return lookUpWeather(region.getNy(), region.getNx());
    }

    public WeatherResponse lookUpWeather(String ny, String nx) {

        log.info("ny, nx : {}, {}", ny, nx);

        StringBuilder urlBuilder = new StringBuilder(url);

        LocalDateTime now = LocalDateTime.now();
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyMMdd"));

        int hour = now.getHour();
        int min = now.getMinute();

        if (min <= 30) {
            hour -= 1;
        }

        String baseTime = null;
        if (hour < 10) {
            baseTime = "0" + hour + "00";
        } else {
            baseTime = hour + "00";
        }

        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            log.info("url : {}", url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();
            conn.disconnect();
            String data = sb.toString();

            log.info("data : {}", data);

            WeatherResponse weatherResponse = new WeatherResponse();

            JSONObject jsonObject = new JSONObject(data);
            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray jArray = items.getJSONArray("item");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject obj = jArray.getJSONObject(i);
                String category = obj.getString("category");
                String fcstValue = obj.getString("fcstValue");

                if (category.equals("SKY")) {
                    if (fcstValue.equals("1")) { //맑은
                        weatherResponse.setWeatherConditions("SUNNY");
                    }else if (fcstValue.equals("2")) { //비오는
                        weatherResponse.setWeatherConditions("RAINY");
                    }else if (fcstValue.equals("3") || fcstValue.equals("4")) { //구름, 흐린
                        weatherResponse.setWeatherConditions("CLOUDY");
                    }
                }

                if (category.equals("T1H")) {
                    weatherResponse.setTemperature(fcstValue);
                }
            }

            return weatherResponse;
        } catch (IOException e) {
            throw new IllegalStateException("An error occurred while receiving weather information.");
        }
    }
}

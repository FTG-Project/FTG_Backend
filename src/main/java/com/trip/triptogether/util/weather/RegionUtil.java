package com.trip.triptogether.util.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RegionUtil {
    private static final Map<String, Position> area = new ConcurrentHashMap<>();

    @Autowired
    public RegionUtil() {
        area.put("Gangwondo", new Position("73", "134"));
        area.put("Busan", new Position("98", "76"));
        area.put("Seoul", new Position("60", "127"));
        area.put("Jeju", new Position("52", "38"));
    }

    public Position getPosition(String region) {
        Position position = area.get(region);
        Position res = new Position(position.getNx(), position.getNy());
        return res;
    }
}

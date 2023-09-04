package com.trip.triptogether.util.weather;

public class Position {
    private String nx;
    private String ny;

    public Position(String x, String y) {
        nx = x;
        ny = y;
    }

    public String getNx() {
        return nx;
    }

    public String getNy() {
        return ny;
    }
}

package com.trip.triptogether.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {
    Long longitude;
    Long latitude;
}

package com.trip.triptogether.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {
    Double longitude;
    Double latitude;
}

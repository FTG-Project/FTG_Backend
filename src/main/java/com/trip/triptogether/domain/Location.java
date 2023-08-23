package com.trip.triptogether.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;


@Embeddable
public class Location {
    Double longitude;
    Double latitude;

}

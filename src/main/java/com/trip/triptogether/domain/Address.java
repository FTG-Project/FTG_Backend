package com.trip.triptogether.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String province;
    private String city;
}

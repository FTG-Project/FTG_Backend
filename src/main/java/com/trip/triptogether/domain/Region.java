package com.trip.triptogether.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Region {
    @Id
    private String regionName;

    private String nx;
    private String ny;
}

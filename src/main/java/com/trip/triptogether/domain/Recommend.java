package com.trip.triptogether.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Recommend {
    @Id @GeneratedValue
    private Long id;

    private String title;

    private String subhead;

    @Embedded
    private Address address;

    private String image;

    private String thumbnail;

    @Lob
    private String content;

    private String detailedAddress;

    private String menu;

    private String time;

    private String phoneNumber;

    private String url;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Area area;
}

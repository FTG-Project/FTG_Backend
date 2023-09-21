package com.trip.triptogether.domain;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    private Double likes;

    private Double rating;

    @OneToMany(mappedBy = "recommend")
    @Builder.Default
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "recommend")
    @Builder.Default
    private List<Scrap> scrapList = new ArrayList<>();

}

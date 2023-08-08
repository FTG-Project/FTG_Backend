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

    @Enumerated(EnumType.STRING)
    private Category category;

    @Lob
    private String content;

    @Embedded
    private Location location;

}

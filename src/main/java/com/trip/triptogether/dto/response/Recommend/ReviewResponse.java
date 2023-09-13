package com.trip.triptogether.dto.response.Recommend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.Review;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReviewResponse {
    private String writer;


    private String content;

    private Double rating;

    private List<String> photoList;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedDate;

    public ReviewResponse(Review review) {
        this.writer = review.getWriter();
        this.content = review.getContent();
        this.createdDate = review.getCreatedDate();
        this.updatedDate = review.getUpdatedDate();
        this.rating = review.getRating();

        this.photoList = review.getPhotoList()
                .stream()
                .map(photo -> photo.getSavedFile())
                .filter(fileUrl -> fileUrl != null)
                .collect(Collectors.toList());
    }
}

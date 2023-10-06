package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.constant.Category;
import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendResponse {
    private Long id;
    private String title;
    private String simpleAddress;
    private String detailAddress;
    private String content;
    private String time;
    private String phoneNumber;
    private String url;
    private String menu;
    private String image;
    private Category category;
    private List<ReviewResponse> reviewList = new ArrayList<>();

    public RecommendResponse(Recommend recommend, List<ReviewResponse> reviewList) {
        id = recommend.getId();
        title = recommend.getTitle();
        simpleAddress = recommend.getAddress().getProvince() + " " + recommend.getAddress().getCity();
        detailAddress = recommend.getDetailedAddress();
        content = recommend.getContent();
        time = recommend.getTime();
        phoneNumber = recommend.getPhoneNumber();
        url = recommend.getUrl();
        menu = recommend.getMenu();
        image = recommend.getImage();
        category = recommend.getCategory();
        this.reviewList = reviewList;
    }
}

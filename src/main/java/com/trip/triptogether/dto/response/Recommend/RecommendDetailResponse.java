package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.constant.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendDetailResponse {
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

    //리뷰 리스트 들어가야함
}

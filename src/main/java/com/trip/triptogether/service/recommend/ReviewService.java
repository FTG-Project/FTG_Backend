package com.trip.triptogether.service.recommend;

import com.trip.triptogether.domain.*;
import com.trip.triptogether.dto.request.recommend.ReviewRequest;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.ReviewResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.recommend.RecommendRepository;
import com.trip.triptogether.repository.recommend.ReviewPhotoRepository;
import com.trip.triptogether.repository.recommend.ReviewRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.service.s3.S3Service;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.trip.triptogether.domain.QUser.user;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ResponseService responseService;
    private final RecommendRepository recommendRepository;
    private final SecurityUtil securityUtil;
    private final S3Service s3Service;

    @Transactional
    public CommonResponse.SingleResponse<ReviewResponse> createReview(ReviewRequest reviewRequest, List<MultipartFile> multipartFileList, Long recommendId) {
        User fromUser = securityUtil.getAuthUserOrThrow();
        User user = userRepository.findById(fromUser.getId()).orElseThrow(
                () -> new NoSuchElementException("user not found"));
        Review review= Review.builder()
                .content(reviewRequest.getContent())
                .rating(reviewRequest.getRating())
                .writer(user.getNickname())
                .user(user)
                .recommend(recommendRepository.findById(recommendId).orElse(null))
                .build();
        user.getReviewList().add(review);
        Review savedReview = reviewRepository.save(review);

        List<ReviewPhoto> photoList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {
            ReviewPhoto photo=ReviewPhoto.builder()
                    .originFile(multipartFile.getOriginalFilename())
                    .fileSize(multipartFile.getSize())
                    .savedFile(s3Service.upload(multipartFile))
                    .review(review)
                    .build();
            photoList.add(photo);
        }
        return responseService.getSingleResponse(HttpStatus.OK.value(), new ReviewResponse(review));
    }
}

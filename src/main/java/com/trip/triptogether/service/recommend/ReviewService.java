package com.trip.triptogether.service.recommend;

import com.trip.triptogether.domain.Photo;
import com.trip.triptogether.domain.Review;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.request.recommend.ReviewRequest;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.ReviewResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.PhotoRepository;
import com.trip.triptogether.repository.recommend.RecommendRepository;
import com.trip.triptogether.repository.recommend.ReviewRepository;
import com.trip.triptogether.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ResponseService responseService;
    private final RecommendRepository recommendRepository;

    @Transactional
    public CommonResponse.SingleResponse<ReviewResponse> createReview(UserPrincipal userPrincipal, ReviewRequest reviewRequest, List<String> files, Long recommendId) {
        User user = userRepository.findByNickname(userPrincipal.getName()).orElseThrow(() -> new UsernameNotFoundException(userPrincipal.getName()));
        Review review= Review.builder()
                .content(reviewRequest.getContent())
                .rating(reviewRequest.getRating())
                .writer(userPrincipal.getName())
                .user(user)
                .recommend(recommendRepository.findById(recommendId).orElse(null))
                .build();
        user.getReviewList().add(review);

        List<String> photoList = new ArrayList<>();

        for (String file : files) {
            Photo photo = new Photo(file,review);
            photoRepository.save(photo);
            review.addPhoto(photo);
            photoList.add(photo.getSavedFile());

        }
        return responseService.getSingleResponse(HttpStatus.OK.value(), new ReviewResponse(review));
    }
}

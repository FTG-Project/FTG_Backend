package com.trip.triptogether.service.recommend;

import com.trip.triptogether.domain.Scrap;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.ScrapResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.recommend.RecommendLikesRepository;
import com.trip.triptogether.repository.recommend.RecommendRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ScrapService {
    private final RecommendLikesRepository recommendLikesRepository;
    private final ResponseService responseService;
    private final RecommendRepository recommendRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    public CommonResponse.SingleResponse<ScrapResponse> addScrap(Long recommendId) {
        User fromUser = securityUtil.getAuthUserOrThrow();
        User toUser = userRepository.findById(fromUser.getId()).orElseThrow(
                () -> new NoSuchElementException("user not found"));
        Scrap scrap = Scrap.builder()
                .user(toUser)
                .recommend(recommendRepository.findById(recommendId).orElse(null))
                .build();
        String message;
        List<Scrap> existingLikes = recommendLikesRepository.findByUserIdAndRecommendId(toUser.getId(), recommendId);

        if (existingLikes.isEmpty()) {
            recommendLikesRepository.save(scrap);
            toUser.getScrapList().add(scrap);
            message = "add";
        } else {
            recommendLikesRepository.delete(existingLikes.get(0));
            toUser.getScrapList().remove(existingLikes.get(0));
            message = "remove";
        }

        return responseService.getSingleResponse(HttpStatus.OK.value(), new ScrapResponse(scrap, message));
    }
}

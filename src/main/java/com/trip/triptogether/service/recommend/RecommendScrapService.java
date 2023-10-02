package com.trip.triptogether.service.recommend;

import com.amazonaws.services.kms.model.NotFoundException;
import com.trip.triptogether.domain.*;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.ScrapResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.board.BoardResponse;
import com.trip.triptogether.repository.recommend.RecommendScrapRepository;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RecommendScrapService {
    private final RecommendScrapRepository scrapRepository;
    private final ResponseService responseService;
    private final RecommendRepository recommendRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    public CommonResponse addScrapToRecommend(Long recommendId) {
        User user= securityUtil.getAuthUserOrThrow();
        User chkUser = userRepository.findById(user.getId())
                .orElseThrow(()->new NotFoundException("could not found user"));

        Recommend recommend = recommendRepository.findById(recommendId)
                .orElseThrow(()->new NotFoundException("could not found recommend"));
        RecommendScrap recommendScrap=RecommendScrap.builder()
                .recommend(recommend)
                .user(user)
                .build();
        scrapRepository.save(recommendScrap);

        return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 하였습니다.");

    }

    public CommonResponse removeScrapFromRecommend(Long boardId) {

        try {
            User user = securityUtil.getAuthUserOrThrow();
            User chkUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new NotFoundException("could not found user"));

            Recommend recommend = recommendRepository.findById(boardId)
                    .orElseThrow(() -> new NotFoundException("could not found board"));


            RecommendScrap deleteScrap = scrapRepository.findByUserAndRecommend(user, recommend);
            if (deleteScrap != null) {
                scrapRepository.delete(deleteScrap);
                return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 게시글이 삭제 되었습니다.");
            } else {
                return responseService.getGeneralResponse(HttpStatus.NOT_FOUND.value(), "스크랩 게시글을 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(),"잘못된 요청입니다.");
        }
    }
}

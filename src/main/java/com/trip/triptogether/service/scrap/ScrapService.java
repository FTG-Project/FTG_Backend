package com.trip.triptogether.service.scrap;

import com.amazonaws.services.kms.model.NotFoundException;
import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.Scrap;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.board.BoardResponse;
import com.trip.triptogether.repository.board.BoardRepository;
import com.trip.triptogether.repository.scrap.ScrapRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.util.SecurityUtil;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScrapService {
    private final UserRepository usersRepository;
    private final ScrapRepository scrapRepository;
    private final BoardRepository boardRepository;
    private final ResponseService responseService;
    private final SecurityUtil securityUtil;

    //스크랩 조회
    public CommonResponse getScrapsByUserId() {
        User user= securityUtil.getAuthUserOrThrow();
        List<Scrap> scrapList = scrapRepository.findAllByUser_Id(user.getId());

        List<BoardResponse> boardResponseList = scrapList.stream()
                .map(scrap -> new BoardResponse(scrap.getBoard()))
                .collect(Collectors.toList());

        return responseService.getListResponse(HttpStatus.OK.value(), boardResponseList);
    }

    //스크랩 추가
    public CommonResponse addScrapToBoard(Long boardID) {
        User user= securityUtil.getAuthUserOrThrow();
        User chkUser = usersRepository.findById(user.getId())
                .orElseThrow(()->new NotFoundException("could not found user"));

        Board board = boardRepository.findById(boardID)
                .orElseThrow(()->new NotFoundException("could not found board"));
        Scrap scrap=Scrap.builder()
                .board(board)
                .user(user)
                .build();
        scrapRepository.save(scrap);

        return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 하였습니다.");

    }

    //스크랩 취소
    public CommonResponse removeScrapFromBoard(Long boardId) {

        try {
            User user = securityUtil.getAuthUserOrThrow();
            User chkUser = usersRepository.findById(user.getId())
                    .orElseThrow(() -> new NotFoundException("could not found user"));

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new NotFoundException("could not found board"));


            Scrap deleteScrap = scrapRepository.findByUserAndBoard(user, board);
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







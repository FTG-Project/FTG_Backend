package com.trip.triptogether.controller.scrap;

import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.service.scrap.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;


    @GetMapping("")
    public ResponseEntity<CommonResponse>  getScrapsByUserId(){
        return ResponseEntity.ok().body(scrapService.getScrapsByUserId());
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<CommonResponse> addScrapToBoard(@PathVariable Long boardId){
        return ResponseEntity.ok().body(scrapService.addScrapToBoard(boardId));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<CommonResponse> removeScrapFromBoard(@PathVariable Long boardId){
        return ResponseEntity.ok().body(scrapService.removeScrapFromBoard(boardId));
    }
}

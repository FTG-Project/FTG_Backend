package com.trip.triptogether.controller.home;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.home.HomeAreaResponse;
import com.trip.triptogether.dto.response.home.HomeResponse;
import com.trip.triptogether.service.recommend.RecommendService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/home")
public class HomeController {
    private final RecommendService recommendService;

    @GetMapping
    public ResponseEntity<CommonResponse.SingleResponse<HomeResponse>> home(@Valid HttpServletResponse response) {
        return ResponseEntity.ok().body(recommendService.homeService());
    }

    @GetMapping("/{Area}")
    public ResponseEntity<CommonResponse.SingleResponse<HomeAreaResponse>> homeArea(@PathVariable("Area") Area area, @Valid
                                     HttpServletResponse response) {

        return ResponseEntity.ok().body(recommendService.areaBeloved(area));
    }
}

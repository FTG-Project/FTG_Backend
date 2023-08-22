package com.trip.triptogether.controller.home;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.dto.response.HomeAreaResponse;
import com.trip.triptogether.service.recommend.RecommendService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/home")
public class HomeController {
    private final RecommendService recommendService;

    @GetMapping("/{Area}")
    public HomeAreaResponse homeArea(@PathVariable("Area") Area area, @Valid
                                     HttpServletResponse response) {

        HomeAreaResponse homeAreaResponse = recommendService.beloved(area);
        return homeAreaResponse;
    }
}

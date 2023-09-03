package com.trip.triptogether.dto.response.scrap;

import com.trip.triptogether.domain.Board;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ScrapResponse {

    private Board scrappedBoards;


    public ScrapResponse(Board scrappedBoards) {

        this.scrappedBoards = scrappedBoards;
    }


}

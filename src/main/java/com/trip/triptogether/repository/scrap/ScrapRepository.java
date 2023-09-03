package com.trip.triptogether.repository.scrap;

import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.Scrap;
import com.trip.triptogether.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap,Long>{
        List<Scrap> findAllByUser_Id(Long user_id);
        Scrap  findByUserAndBoard(User users, Board board);
}

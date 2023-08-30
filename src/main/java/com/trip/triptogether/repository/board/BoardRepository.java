package com.trip.triptogether.repository.board;

import com.trip.triptogether.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Long>,BoardRepositoryCustom {
}

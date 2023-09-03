package com.trip.triptogether.repository;

import com.trip.triptogether.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo,Long> {
    List<Photo> findByBoardId(Long boardId);
    Optional<Photo> deleteByBoardId(Long boardId);
}

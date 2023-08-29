package com.trip.triptogether.repository.recommend;

import com.trip.triptogether.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Recommend, Long> {
}

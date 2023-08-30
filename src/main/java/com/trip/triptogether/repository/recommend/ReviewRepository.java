package com.trip.triptogether.repository.recommend;

import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}

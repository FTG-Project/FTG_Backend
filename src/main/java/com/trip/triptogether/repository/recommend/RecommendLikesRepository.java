package com.trip.triptogether.repository.recommend;

import com.trip.triptogether.domain.RecommendLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendLikesRepository extends JpaRepository<RecommendLikes, Long> {
}

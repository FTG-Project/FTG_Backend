package com.trip.triptogether.repository.recommend;

import com.trip.triptogether.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendLikesRepository extends JpaRepository<Scrap, Long> {

    @Query("select r from Scrap r where r.user.id = :userId and r.recommend.id = :recommendId")
    List<Scrap> findByUserIdAndRecommendId(@Param("userId") Long userId, @Param("recommendId") Long recommendId);
}

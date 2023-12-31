package com.trip.triptogether.repository.recommend;

import com.trip.triptogether.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendScrapRepository extends JpaRepository<RecommendScrap, Long> {

    RecommendScrap  findByUserAndRecommend(User users, Recommend recommend);

    @Query("select r from RecommendScrap r where r.user.id = :userId and r.recommend.id = :recommendId")
    List<RecommendScrap> findByUserIdAndRecommendId(@Param("userId") Long userId, @Param("recommendId") Long recommendId);
}

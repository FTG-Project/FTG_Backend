package com.trip.triptogether.repository.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.domain.Recommend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RecommendRepository extends JpaRepository<Recommend, Long>, RecommendRepositoryCustom {

    @Query(value = "SELECT * FROM recommend ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Recommend> findRandomRecommend();

    @Query("select r from Recommend r left join fetch r.reviewList where r.id = :id")
    Recommend findAllFetchJoinReview(Long id);
}

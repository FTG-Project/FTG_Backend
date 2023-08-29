package com.trip.triptogether.repository.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.dto.response.Recommend.RecommendBelovedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    @Query("SELECT new " +
            "com.trip.triptogether.dto.response.Recommend.RecommendBelovedResponse(p.id, p.title, p.address.province, p.address.city, p.thumbnail, p.rating, p.likes) " +
            "FROM Recommend p " +
            "WHERE p.category = :category AND p.area = :area " +
            "ORDER BY (p.likes * 0.5 + p.rating * 0.5) DESC")
    List<RecommendBelovedResponse> findTop5RecommendByCombinedScore(@Param("category") Category category, @Param("area") Area area, Pageable pageable);

    List<Recommend> findTop10ByOrderByRatingDesc();

    List<Recommend> findTop10ByOrderByRatingAsc();

    @Query(value = "SELECT * FROM recommend ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Recommend> findRandomRecommend();

    List<Recommend> findByCategoryAndAreaOrderById(Category category, Area area);

    List<Recommend> findByCategoryAndAreaOrderByRatingDesc(Category category, Area area);

    List<Recommend> findByCategoryAndAreaOrderByLikesDesc(Category category, Area area);
}

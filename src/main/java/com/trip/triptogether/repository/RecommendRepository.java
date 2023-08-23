package com.trip.triptogether.repository;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.dto.response.Recommend.AttractionBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.MedicalFacilityBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.RestaurantBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.ShowBelovedResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    @Query("SELECT new " +
            "com.trip.triptogether.dto.response.Recommend.AttractionBelovedResponse(p.id, p.title, p.address.province, p.address.city, p.thumbnail, p.rating, p.likes) " +
            "FROM Recommend p " +
            "WHERE p.category = 'ATTRACTION' AND p.area = :area " +
            "ORDER BY (p.likes * 0.5 + p.rating * 0.5) DESC")
    List<AttractionBelovedResponse> findTop5AttractionByCombinedScore(Area area);

    @Query("SELECT new " +
            "com.trip.triptogether.dto.response.Recommend.RestaurantBelovedResponse(p.id, p.title, p.address.province, p.address.city, p.thumbnail, p.rating, p.likes) " +
            "FROM Recommend p " +
            "WHERE p.category = 'RESTAURANT' AND p.area = :area " +
            "ORDER BY (p.likes * 0.5 + p.rating * 0.5) DESC")
    List<RestaurantBelovedResponse> findTop5RestaurantByCombinedScore(Area area);

    @Query("SELECT new " +
            "com.trip.triptogether.dto.response.Recommend.ShowBelovedResponse(p.id, p.title, p.address.province, p.address.city, p.thumbnail, p.rating, p.likes) " +
            "FROM Recommend p " +
            "WHERE p.category = 'SHOW' AND p.area = :area " +
            "ORDER BY (p.likes * 0.5 + p.rating * 0.5) DESC")
    List<ShowBelovedResponse> findTop5ShowByCombinedScore(Area area);

    @Query("SELECT new " +
            "com.trip.triptogether.dto.response.Recommend.MedicalFacilityBelovedResponse(p.id, p.title, p.address.province, p.address.city, p.thumbnail, p.rating, p.likes) " +
            "FROM Recommend p " +
            "WHERE p.category = 'MEDICALFACILITY' AND p.area = :area " +
            "ORDER BY (p.likes * 0.5 + p.rating * 0.5) DESC")
    List<MedicalFacilityBelovedResponse> findTop5MedicalFacilityByCombinedScore(Area area);

    List<Recommend> findTop10ByOrderByRatingDesc();

    List<Recommend> findTop10ByOrderByRatingAsc();

    @Query(value = "SELECT * FROM recommend ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Recommend> findRandomRecommend();
}

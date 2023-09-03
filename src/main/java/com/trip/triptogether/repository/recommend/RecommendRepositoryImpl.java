package com.trip.triptogether.repository.recommend;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.response.Recommend.RecommendListResponse;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.trip.triptogether.domain.QRecommend.recommend;
import static com.trip.triptogether.domain.QReview.review;

public class RecommendRepositoryImpl implements RecommendRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RecommendRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<RecommendListResponse> recommendList(Category category, Area area) {
        return queryFactory
                .select(Projections.constructor(
                        RecommendListResponse.class,
                        recommend.id,
                        recommend.title,
                        recommend.thumbnail,
                        recommend.address.city,
                        recommend.likes,
                        review.rating.avg()))
                .from(recommend)
                .leftJoin(review).on(recommend.id.eq(review.recommend.id))
                .where(recommend.area.eq(area), recommend.category.eq(category))
                .groupBy(recommend.id)
                .fetch();


    }
}

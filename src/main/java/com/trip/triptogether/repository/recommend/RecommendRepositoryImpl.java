package com.trip.triptogether.repository.recommend;

import com.querydsl.core.types.Projections;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.response.Recommend.RecommendListResponse;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.trip.triptogether.domain.QRecommend.recommend;
import static com.trip.triptogether.domain.QRecommendLikes.recommendLikes;
import static com.trip.triptogether.domain.QReview.review;

public class RecommendRepositoryImpl implements RecommendRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RecommendRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<RecommendListResponse> recommendList(Category category, Area area) {
        NumberPath<Long> idPath = Expressions.numberPath(Long.class, "id");
        NumberPath<Double> ratingPath = Expressions.numberPath(Double.class, "rating");
        NumberPath<Double> likesPath = Expressions.numberPath(Double.class, "likes");

        return queryFactory
                .select(
                        Projections.constructor(
                                RecommendListResponse.class,
                                recommend.id,
                                recommend.title,
                                recommend.thumbnail,
                                Expressions.stringTemplate("concat({0}, ' ', {1})", recommend.address.province, recommend.address.city).as("address"),
                                JPAExpressions
                                        .select(recommendLikes.user.id.count())
                                        .from(recommendLikes)
                                        .where(recommendLikes.recommend.id.eq(recommend.id)),
                                review.rating.avg()
                        )
                )
                .from(recommend)
                .leftJoin(review).on(recommend.id.eq(review.recommend.id))
                .where(recommend.area.eq(area), recommend.category.eq(category))
                .groupBy(recommend.id)
                .fetch();
    }
}

package com.trip.triptogether.repository.recommend;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.response.Recommend.RecommendBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendBestResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendListResponse;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.trip.triptogether.domain.QRecommend.recommend;
import static com.trip.triptogether.domain.QScrap.scrap;
import static com.trip.triptogether.domain.QReview.review;

public class RecommendRepositoryImpl implements RecommendRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RecommendRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<RecommendListResponse> recommendList(Category category, Area area) {

        JPQLQuery<Long> likes = JPAExpressions
                .select(scrap.user.id.count())
                .from(scrap)
                .where(scrap.recommend.id.eq(recommend.id));


        return queryFactory
                .select(
                        Projections.constructor(
                                RecommendListResponse.class,
                                recommend.id,
                                recommend.title,
                                recommend.thumbnail,
                                Expressions.stringTemplate("concat({0}, ' ', {1})", recommend.address.province, recommend.address.city).as("address"),
                                likes,
                                review.rating.avg()
                        )
                )
                .from(recommend)
                .leftJoin(review).on(recommend.id.eq(review.recommend.id))
                .where(recommend.area.eq(area), recommend.category.eq(category))
                .groupBy(recommend.id)
                .fetch();
    }

    @Override
    public List<RecommendBestResponse> findTop10() {

        JPQLQuery<Long> likes = JPAExpressions
                .select(scrap.user.id.count())
                .from(scrap)
                .where(scrap.recommend.id.eq(recommend.id));

        return queryFactory
                .select(
                        Projections.constructor(
                                RecommendBestResponse.class,
                                recommend.id,
                                recommend.title,
                                recommend.address.city,
                                recommend.thumbnail,
                                likes,
                                review.rating.avg()
                        )
                )
                .from(recommend)
                .leftJoin(review).on(recommend.id.eq(review.recommend.id))
                .groupBy(recommend.id)
                .limit(10)
                .fetch();
    }

    @Override
    public List<RecommendBelovedResponse> findTop5RecommendByAreaAndCategoryOrderByRating(Area area, Category category) {
        JPQLQuery<Long> likes = JPAExpressions
                .select(scrap.user.id.count())
                .from(scrap)
                .where(scrap.recommend.id.eq(recommend.id));

        return queryFactory
                .select(
                        Projections.constructor(
                                RecommendBelovedResponse.class,
                                recommend.id,
                                recommend.title,
                                Expressions.stringTemplate("concat({0}, ' ', {1})", recommend.address.province, recommend.address.city).as("address"),
                                recommend.thumbnail,
                                review.rating.avg(),
                                likes
                        )
                )
                .from(recommend)
                .where(recommend.area.eq(area), recommend.category.eq(category))
                .leftJoin(review).on(recommend.id.eq(review.recommend.id))
                .groupBy(recommend.id)
                .orderBy(review.rating.avg().desc())
                .limit(5)
                .fetch();
    }
}

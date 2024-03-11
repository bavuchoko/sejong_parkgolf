package com.pjs.golf.game.repository.querydsl;

import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.common.jpa.QuerydslCommonMethod;
import com.pjs.golf.game.entity.Game;

import com.pjs.golf.game.entity.QGame;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class GameJpaQuerydslSupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManage;

    public GameJpaQuerydslSupport(JPAQueryFactory jpaQueryFactory, EntityManager entityManage) {
        super(Game.class);
        this.queryFactory = jpaQueryFactory;
        this.entityManage = entityManage;
    }
    QGame game = QGame.game;
    public Page<Game> getGameListBySearch(SearchDto search, Pageable pageable) {

        JPAQuery<Game> query= queryFactory.selectFrom(game).where(
                        eqAddressOrLikeDetailAndDate(search)
                )
                .orderBy(QuerydslCommonMethod.getOrderList(pageable.getSort(), Game.class).stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                ;
        long totalCount = query.stream().count();
        List<Game> result = getQuerydsl().applyPagination(pageable,query).fetch();
        return new PageImpl<>(result,pageable,totalCount);
    }


    private BooleanExpression eqAddressOrLikeDetailAndDate(SearchDto search) {
        if (StringUtils.hasText(search.getSearchTxt()))
            return game.fields.address.eq(search.getSearchTxt())
                    .or(game.detail.contains(search.getSearchTxt()))
                    .and( game.playDate.between(search.getStartDate(),search.getEndDate()));
        return game.playDate.between(search.getStartDate(),search.getEndDate());
    }

}

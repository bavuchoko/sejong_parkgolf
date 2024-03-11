package com.pjs.golf.warmup.repository.querydsl;

import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.common.jpa.QuerydslCommonMethod;
import com.pjs.golf.warmup.entity.QWarmupGame;
import com.pjs.golf.warmup.entity.WarmupGame;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class WarmupGameQuerydslSupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;
    public WarmupGameQuerydslSupport(JPAQueryFactory queryFactory) {
        super(WarmupGame.class);
        this.queryFactory = queryFactory;
    }

    QWarmupGame warmupGame = QWarmupGame.warmupGame;



    public Page<WarmupGame> getWarumupGameListBetweenDate(SearchDto search, Pageable pageable) {

        JPAQuery<WarmupGame> query= queryFactory.selectFrom(warmupGame).where(
                        warmupGame.playDate.between(search.getStartDate(),search.getEndDate())
                )
                .orderBy(QuerydslCommonMethod.getOrderList(pageable.getSort(), WarmupGame.class).stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                ;
        List<WarmupGame> result = getQuerydsl().applyPagination(pageable,query).fetch();
        return new PageImpl<>(result,pageable, result.size());
    }
}

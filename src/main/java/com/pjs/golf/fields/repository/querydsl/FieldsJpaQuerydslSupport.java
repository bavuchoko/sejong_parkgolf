package com.pjs.golf.fields.repository.querydsl;

import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.common.jpa.QuerydslCommonMethod;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.fields.entity.QFields;
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
public class FieldsJpaQuerydslSupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManage;
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     * @param queryFactory
     * @param entityManage
     */
    public FieldsJpaQuerydslSupport(JPAQueryFactory jpaQueryFactory, EntityManager entityManage) {
        super(Fields.class);
        this.queryFactory = jpaQueryFactory;
        this.entityManage = entityManage;
    }

    QFields fields = QFields.fields;
    public Page<Fields> getFieldsListBySearCh(SearchDto search, Pageable pageable) {

        JPAQuery<Fields> query= queryFactory.selectFrom(fields).where(
                        containCityOrName(search)
                )
                .orderBy(QuerydslCommonMethod.getOrderList(pageable.getSort(), Fields.class).stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                ;
        long totalCount = query.stream().count();
        List<Fields> result = getQuerydsl().applyPagination(pageable,query).fetch();
        return new PageImpl<>(result,pageable,totalCount);
    }


    private BooleanExpression eqCity(String address) {
        if (StringUtils.hasText(address)) return fields.city.eq(address);
        return null;
    }

    private BooleanExpression containCityOrName(SearchDto search) {
        if (StringUtils.hasText(search.getSearchTxt()))
            return fields.name.contains(search.getSearchTxt())
                    .or(fields.city.contains(search.getSearchTxt()));
        return null;
    }

}

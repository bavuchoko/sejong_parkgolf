package com.pjs.golf.game.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.catalina.Store;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ScroeJpaQuerydslSupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManage;

    public ScroeJpaQuerydslSupport(JPAQueryFactory jpaQueryFactory, EntityManager entityManage, EntityManager entityManager) {
        super(Store.class);
        this.queryFactory = jpaQueryFactory;
        this.entityManage = entityManage;
    }



}

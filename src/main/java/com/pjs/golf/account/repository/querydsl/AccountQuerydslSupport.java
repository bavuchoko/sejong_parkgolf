package com.pjs.golf.account.repository.querydsl;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.entity.QAccount;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountQuerydslSupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;
    public AccountQuerydslSupport(JPAQueryFactory queryFactory) {
        super(Account.class);
        this.queryFactory = queryFactory;
    }

    QAccount account = QAccount.account;

    public List getTempUsersByUserNames(List names) {
        List<Account> query= queryFactory.selectFrom(account).where(
                        account.name.in(names).and(
                        account.username.like("temp%"))
                ).fetch();
        return query;
    }

    public void createTmepUsers(List noneMatched) {

    }
}

package com.pjs.golf.common.jpa;

import com.pjs.golf.game.entity.Game;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class QuerydslCommonMethod {
    public static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }

    public static  <T> List<OrderSpecifier> getOrderList(Sort sort, Class<T> clazz) {
        List<OrderSpecifier> orders = new ArrayList<>();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder orderByExpression = new PathBuilder(clazz,camelCase(clazz.getSimpleName()));
            orders.add(new OrderSpecifier(direction, orderByExpression.get(order.getProperty())));
        });
        return orders;
    }
    private static String camelCase(String clazzName) {
        return Character.toLowerCase(clazzName.charAt(0)) + clazzName.substring(1);
    }
}

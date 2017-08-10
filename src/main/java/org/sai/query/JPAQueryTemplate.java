package org.sai.query;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Data;

import javax.persistence.EntityManager;

/**
 * Created by saipkri on 09/08/17.
 */
@Data
public class JPAQueryTemplate extends QueryTemplate<Object, EntityPath<?>, JPAQuery<?>> {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public JPAQueryTemplate(final EntityManager entityManager) {
        this.entityManager = entityManager;
        jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public JPAQuery<?> query() {
        return jpaQueryFactory.query();
    }

    @Override
    public JPAQuery<?> query(@SuppressWarnings("unused") final EntityPath<?> entityPath) {
        return query();
    }
}

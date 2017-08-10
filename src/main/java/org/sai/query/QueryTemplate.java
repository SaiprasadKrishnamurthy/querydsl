package org.sai.query;

import com.querydsl.core.SimpleQuery;
import com.querydsl.core.types.EntityPath;

/**
 * Created by saipkri on 09/08/17.
 */
public abstract class QueryTemplate<MODEL extends Object, METAMODEL extends EntityPath<? extends Object>, T extends SimpleQuery<? extends Object>> {
    public abstract T query();
    public abstract T query(final METAMODEL metamodel);
}

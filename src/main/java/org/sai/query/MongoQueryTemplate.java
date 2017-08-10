package org.sai.query;

import com.querydsl.core.types.EntityPath;
import com.querydsl.mongodb.morphia.MorphiaQuery;
import lombok.Data;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by saipkri on 09/08/17.
 */
@Data
public class MongoQueryTemplate<MODEL extends Object, METAMODEL extends EntityPath<? extends Object>> extends QueryTemplate<MODEL, METAMODEL, MorphiaQuery<MODEL>> {

    private final Morphia morphia;
    private final Datastore datastore;

    public MongoQueryTemplate(final Morphia morphia, final Datastore datastore) {
        this.morphia = morphia;
        this.datastore = datastore;
    }

    @Override
    public MorphiaQuery<MODEL> query() {
        throw new UnsupportedOperationException("Use query(metamodel) method instead for this template.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public MorphiaQuery<MODEL> query(METAMODEL metamodel) {
        return new MorphiaQuery(morphia, datastore, metamodel);
    }


}

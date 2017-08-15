package org.sai.querydsl.intro;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by saipkri on 14/08/17.
 */
public class ExpressionUtil {

    private ExpressionUtil() {
    }

    private static final Map<Class<? extends Path>, BiFunction<Path, Operation, BooleanExpression>> FILTER_EXPRESSIONS = new HashMap<>();

    static {
        FILTER_EXPRESSIONS.put(StringPath.class, (Path _field, Operation _stringop) -> {
            StringPath field = (StringPath) _field;
            Operation<String> stringop = _stringop;
            switch (stringop.getOperator()) {
                case "=":
                    return field.eq(stringop.getOperands().get(0));
                case "contains":
                    return field.contains(stringop.getOperands().get(0));
                case "in":
                    return field.in(stringop.getOperands());
                case "!=":
                    return field.ne(stringop.getOperands().get(0));
                default:
                    throw new IllegalArgumentException("Unknown operation for a string type: " + stringop);
            }
        });
    }

    public static Path<?> field(final EntityPathBase entity, final String fieldName) {
        try {
            if (entity.getClass().getDeclaredField(fieldName).getType().equals(StringPath.class)) {
                return Expressions.stringPath(entity, fieldName);
            } else if (entity.getClass().getDeclaredField(fieldName).getType().equals(NumberPath.class)) {
                return Expressions.numberPath(Double.class, entity, fieldName);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    public static BooleanExpression innerJoinOn(final Path<?> path1, final Path<?> path2) {
        if (path1.getClass().equals(StringPath.class)) {
            return ((StringPath) path1).eq((StringPath) path2);
        } else if (path1.getClass().equals(NumberPath.class)) {
            return ((NumberPath) path1).eq(path2);
        }
        return null;
    }

    public static <T extends Path, O extends Operation> BooleanExpression applyFilter(final T path, final O operation) {
        return FILTER_EXPRESSIONS.get(path.getClass()).apply(path, operation);
    }

}

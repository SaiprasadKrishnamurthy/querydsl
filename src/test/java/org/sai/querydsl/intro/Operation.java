package org.sai.querydsl.intro;

import java.util.List;

/**
 * Created by saipkri on 14/08/17.
 */
public class Operation<T> {
    private final String operator;
    private final List<T> operands;

    public Operation(String operator, List<T> operands) {
        this.operator = operator;
        this.operands = operands;
    }

    public String getOperator() {
        return operator;
    }

    public List<T> getOperands() {
        return operands;
    }
}

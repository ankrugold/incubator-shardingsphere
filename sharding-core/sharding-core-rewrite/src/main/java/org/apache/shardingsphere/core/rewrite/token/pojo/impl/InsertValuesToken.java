/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.rewrite.token.pojo.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.complex.ComplexExpressionSegment;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.simple.LiteralExpressionSegment;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.simple.ParameterMarkerExpressionSegment;
import org.apache.shardingsphere.core.rewrite.token.pojo.Alterable;
import org.apache.shardingsphere.core.rewrite.token.pojo.SQLToken;
import org.apache.shardingsphere.core.rewrite.token.pojo.Substitutable;
import org.apache.shardingsphere.core.route.type.RoutingUnit;
import org.apache.shardingsphere.core.rule.DataNode;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Insert values token.
 *
 * @author maxiaoguang
 * @author panjuan
 */
public final class InsertValuesToken extends SQLToken implements Substitutable, Alterable {
    
    @Getter
    private final int stopIndex;
    
    private final List<InsertValueToken> insertValueTokens;
    
    public InsertValuesToken(final int startIndex, final int stopIndex) {
        super(startIndex);
        this.stopIndex = stopIndex;
        insertValueTokens = new LinkedList<>();
    }
    
    /**
     * Add insert value token.
     * 
     * @param values values
     * @param dataNodes data nodes
     */
    public void addInsertValueToken(final List<ExpressionSegment> values, final Collection<DataNode> dataNodes) {
        insertValueTokens.add(new InsertValueToken(values, dataNodes));
    }
    
    @Override
    public String toString(final RoutingUnit routingUnit, final Map<String, String> logicAndActualTables) {
        StringBuilder result = new StringBuilder();
        appendUnits(routingUnit, result);
        result.delete(result.length() - 2, result.length());
        return result.toString();
    }
    
    private void appendUnits(final RoutingUnit routingUnit, final StringBuilder result) {
        for (InsertValueToken each : insertValueTokens) {
            if (isToAppendInsertOptimizeResult(routingUnit, each)) {
                result.append(each).append(", ");
            }
        }
    }
    
    private boolean isToAppendInsertOptimizeResult(final RoutingUnit routingUnit, final InsertValueToken unit) {
        if (unit.getDataNodes().isEmpty() || null == routingUnit) {
            return true;
        }
        for (DataNode each : unit.getDataNodes()) {
            if (routingUnit.getTableUnit(each.getDataSourceName(), each.getTableName()).isPresent()) {
                return true;
            }
        }
        return false;
    }
    
    @RequiredArgsConstructor
    private final class InsertValueToken {
    
        private final List<ExpressionSegment> values;
    
        @Getter
        private final Collection<DataNode> dataNodes;
    
        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("(");
            for (int i = 0; i < values.size(); i++) {
                result.append(getValue(i)).append(", ");
            }
            result.delete(result.length() - 2, result.length()).append(")");
            return result.toString();
        }
        
        private String getValue(final int index) {
            ExpressionSegment expressionSegment = values.get(index);
            if (expressionSegment instanceof ParameterMarkerExpressionSegment) {
                return "?";
            } else if (expressionSegment instanceof LiteralExpressionSegment) {
                Object literals = ((LiteralExpressionSegment) expressionSegment).getLiterals();
                return literals instanceof String ? String.format("'%s'", ((LiteralExpressionSegment) expressionSegment).getLiterals()) : literals.toString();
            }
            return ((ComplexExpressionSegment) expressionSegment).getText();
        }
    }
}

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

package org.apache.shardingsphere.core.rewrite.token.generator.collection.impl;

import com.google.common.base.Preconditions;
import org.apache.shardingsphere.core.optimize.segment.select.projection.DerivedColumn;
import org.apache.shardingsphere.core.optimize.segment.select.projection.impl.AggregationDistinctProjection;
import org.apache.shardingsphere.core.optimize.statement.SQLStatementContext;
import org.apache.shardingsphere.core.optimize.statement.impl.SelectSQLStatementContext;
import org.apache.shardingsphere.core.rewrite.token.generator.IgnoreForSingleRoute;
import org.apache.shardingsphere.core.rewrite.token.generator.collection.CollectionSQLTokenGenerator;
import org.apache.shardingsphere.core.rewrite.token.pojo.impl.AggregationDistinctToken;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Aggregation distinct token generator.
 *
 * @author panjuan
 */
public final class AggregationDistinctTokenGenerator implements CollectionSQLTokenGenerator, IgnoreForSingleRoute {
    
    @Override
    public Collection<AggregationDistinctToken> generateSQLTokens(final SQLStatementContext sqlStatementContext) {
        if (!(sqlStatementContext instanceof SelectSQLStatementContext)) {
            return Collections.emptyList();
        }
        Collection<AggregationDistinctToken> result = new LinkedList<>();
        for (AggregationDistinctProjection each : ((SelectSQLStatementContext) sqlStatementContext).getProjectionsContext().getAggregationDistinctProjections()) {
            result.add(createAggregationDistinctToken(each));
        }
        return result;
    }
    
    private AggregationDistinctToken createAggregationDistinctToken(final AggregationDistinctProjection projection) {
        Preconditions.checkArgument(projection.getAlias().isPresent());
        String derivedAlias = DerivedColumn.isDerivedColumnName(projection.getAlias().get()) ? projection.getAlias().get() : null;
        return new AggregationDistinctToken(projection.getStartIndex(), projection.getStopIndex(), projection.getDistinctInnerExpression(), derivedAlias);
    }
}

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

package org.apache.shardingsphere.core.rewrite.token.generator.optional.impl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.shardingsphere.core.optimize.segment.select.projection.Projection;
import org.apache.shardingsphere.core.optimize.segment.select.projection.impl.AggregationDistinctProjection;
import org.apache.shardingsphere.core.optimize.segment.select.projection.impl.AggregationProjection;
import org.apache.shardingsphere.core.optimize.segment.select.projection.impl.DerivedProjection;
import org.apache.shardingsphere.core.optimize.statement.SQLStatementContext;
import org.apache.shardingsphere.core.optimize.statement.impl.SelectSQLStatementContext;
import org.apache.shardingsphere.core.rewrite.token.generator.IgnoreForSingleRoute;
import org.apache.shardingsphere.core.rewrite.token.generator.optional.OptionalSQLTokenGenerator;
import org.apache.shardingsphere.core.rewrite.token.pojo.impl.ProjectionsToken;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Projections token generator.
 *
 * @author zhangliang
 */
public final class ProjectionsTokenGenerator implements OptionalSQLTokenGenerator, IgnoreForSingleRoute {
    
    @Override
    public Optional<ProjectionsToken> generateSQLToken(final SQLStatementContext sqlStatementContext) {
        if (!(sqlStatementContext instanceof SelectSQLStatementContext)) {
            return Optional.absent();
        }
        Collection<String> derivedProjectionTexts = getDerivedProjectionTexts((SelectSQLStatementContext) sqlStatementContext);
        return derivedProjectionTexts.isEmpty() ? Optional.<ProjectionsToken>absent() : Optional.of(new ProjectionsToken(
                ((SelectSQLStatementContext) sqlStatementContext).getProjectionsContext().getStopIndex() + 1 + " ".length(), derivedProjectionTexts));
    }
    
    private Collection<String> getDerivedProjectionTexts(final SelectSQLStatementContext selectSQLStatementContext) {
        Collection<String> result = new LinkedList<>();
        for (Projection each : selectSQLStatementContext.getProjectionsContext().getProjections()) {
            if (each instanceof AggregationProjection && !((AggregationProjection) each).getDerivedAggregationProjections().isEmpty()) {
                result.addAll(Lists.transform(((AggregationProjection) each).getDerivedAggregationProjections(), new Function<AggregationProjection, String>() {
                    
                    @Override
                    public String apply(final AggregationProjection input) {
                        return getDerivedProjectionText(input);
                    }
                }));
            } else if (each instanceof DerivedProjection) {
                result.add(getDerivedProjectionText(each));
            }
        }
        return result;
    }
    
    private String getDerivedProjectionText(final Projection projection) {
        Preconditions.checkState(projection.getAlias().isPresent());
        if (projection instanceof AggregationDistinctProjection) {
            return ((AggregationDistinctProjection) projection).getDistinctInnerExpression() + " AS " + projection.getAlias().get() + " ";
        }
        return projection.getExpression() + " AS " + projection.getAlias().get() + " ";
    }
}

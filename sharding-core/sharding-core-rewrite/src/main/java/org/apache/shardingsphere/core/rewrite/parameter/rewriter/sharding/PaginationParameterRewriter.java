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

package org.apache.shardingsphere.core.rewrite.parameter.rewriter.sharding;

import com.google.common.base.Optional;
import lombok.Setter;
import org.apache.shardingsphere.core.optimize.segment.select.pagination.PaginationContext;
import org.apache.shardingsphere.core.optimize.statement.SQLStatementContext;
import org.apache.shardingsphere.core.optimize.statement.impl.SelectSQLStatementContext;
import org.apache.shardingsphere.core.rewrite.parameter.builder.ParameterBuilder;
import org.apache.shardingsphere.core.rewrite.parameter.builder.standard.StandardParameterBuilder;
import org.apache.shardingsphere.core.rewrite.parameter.rewriter.ParameterRewriter;
import org.apache.shardingsphere.core.rewrite.token.generator.SQLRouteResultAware;
import org.apache.shardingsphere.core.route.SQLRouteResult;

import java.util.List;

/**
 * Pagination parameter rewriter.
 *
 * @author zhangliang
 */
@Setter
public final class PaginationParameterRewriter implements ParameterRewriter, SQLRouteResultAware {
    
    private SQLRouteResult sqlRouteResult;
    
    @Override
    public void rewrite(final SQLStatementContext sqlStatementContext, final List<Object> parameters, final ParameterBuilder parameterBuilder) {
        if (isNeedRewritePagination(sqlRouteResult)) {
            PaginationContext pagination = ((SelectSQLStatementContext) sqlRouteResult.getSqlStatementContext()).getPaginationContext();
            Optional<Integer> offsetParameterIndex = pagination.getOffsetParameterIndex();
            if (offsetParameterIndex.isPresent()) {
                rewriteOffset(pagination, offsetParameterIndex.get(), (StandardParameterBuilder) parameterBuilder);
            }
            Optional<Integer> rowCountParameterIndex = pagination.getRowCountParameterIndex();
            if (rowCountParameterIndex.isPresent()) {
                rewriteRowCount(pagination, rowCountParameterIndex.get(), (StandardParameterBuilder) parameterBuilder);
            }
        }
    }
    
    private boolean isNeedRewritePagination(final SQLRouteResult sqlRouteResult) {
        return sqlRouteResult.getSqlStatementContext() instanceof SelectSQLStatementContext
                && ((SelectSQLStatementContext) sqlRouteResult.getSqlStatementContext()).getPaginationContext().isHasPagination() && !sqlRouteResult.getRoutingResult().isSingleRouting();
    }
    
    private void rewriteOffset(final PaginationContext pagination, final int offsetParameterIndex, final StandardParameterBuilder parameterBuilder) {
        parameterBuilder.getReplacedIndexAndParameters().put(offsetParameterIndex, pagination.getRevisedOffset());
    }
    
    private void rewriteRowCount(final PaginationContext pagination, final int rowCountParameterIndex, final StandardParameterBuilder parameterBuilder) {
        parameterBuilder.getReplacedIndexAndParameters().put(rowCountParameterIndex, pagination.getRevisedRowCount((SelectSQLStatementContext) sqlRouteResult.getSqlStatementContext()));
    }
}

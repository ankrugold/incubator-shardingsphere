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

import com.google.common.base.Optional;
import org.apache.shardingsphere.core.optimize.segment.select.pagination.PaginationContext;
import org.apache.shardingsphere.core.optimize.statement.SQLStatementContext;
import org.apache.shardingsphere.core.optimize.statement.impl.SelectSQLStatementContext;
import org.apache.shardingsphere.core.parse.sql.segment.dml.pagination.NumberLiteralPaginationValueSegment;
import org.apache.shardingsphere.core.rewrite.token.generator.IgnoreForSingleRoute;
import org.apache.shardingsphere.core.rewrite.token.generator.optional.OptionalSQLTokenGenerator;
import org.apache.shardingsphere.core.rewrite.token.pojo.impl.RowCountToken;

/**
 * Row count token generator.
 *
 * @author panjuan
 */
public final class RowCountTokenGenerator implements OptionalSQLTokenGenerator, IgnoreForSingleRoute {
    
    @Override
    public Optional<RowCountToken> generateSQLToken(final SQLStatementContext sqlStatementContext) {
        if (!(sqlStatementContext instanceof SelectSQLStatementContext)) {
            return Optional.absent();
        }
        PaginationContext pagination = ((SelectSQLStatementContext) sqlStatementContext).getPaginationContext();
        return pagination.getRowCountSegment().isPresent() && pagination.getRowCountSegment().get() instanceof NumberLiteralPaginationValueSegment
                ? Optional.of(new RowCountToken(pagination.getRowCountSegment().get().getStartIndex(), pagination.getRowCountSegment().get().getStopIndex(), 
                pagination.getRevisedRowCount((SelectSQLStatementContext) sqlStatementContext))) : Optional.<RowCountToken>absent();
    }
}

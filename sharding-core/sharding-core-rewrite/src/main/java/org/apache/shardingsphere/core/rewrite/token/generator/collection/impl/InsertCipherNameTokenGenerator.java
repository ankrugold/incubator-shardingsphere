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

import com.google.common.base.Optional;
import lombok.Setter;
import org.apache.shardingsphere.core.optimize.statement.SQLStatementContext;
import org.apache.shardingsphere.core.optimize.statement.impl.InsertSQLStatementContext;
import org.apache.shardingsphere.core.parse.sql.segment.dml.column.ColumnSegment;
import org.apache.shardingsphere.core.parse.sql.segment.dml.column.InsertColumnsSegment;
import org.apache.shardingsphere.core.rewrite.token.generator.EncryptRuleAware;
import org.apache.shardingsphere.core.rewrite.token.generator.collection.CollectionSQLTokenGenerator;
import org.apache.shardingsphere.core.rewrite.token.pojo.impl.InsertCipherNameToken;
import org.apache.shardingsphere.core.rule.EncryptRule;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

/**
 * Insert logic column names token generator.
 *
 * @author panjuan
 */
@Setter
public final class InsertCipherNameTokenGenerator implements CollectionSQLTokenGenerator, EncryptRuleAware {
    
    private EncryptRule encryptRule;
    
    @Override
    public Collection<InsertCipherNameToken> generateSQLTokens(final SQLStatementContext sqlStatementContext) {
        if (!isNeedToGenerateSQLToken(sqlStatementContext)) {
            return Collections.emptyList();
        }
        return createInsertColumnTokens((InsertSQLStatementContext) sqlStatementContext);
    }
    
    private boolean isNeedToGenerateSQLToken(final SQLStatementContext sqlStatementContext) {
        Optional<InsertColumnsSegment> insertColumnsSegment = sqlStatementContext.getSqlStatement().findSQLSegment(InsertColumnsSegment.class);
        return sqlStatementContext instanceof InsertSQLStatementContext && insertColumnsSegment.isPresent() && !insertColumnsSegment.get().getColumns().isEmpty();
    }
    
    private Collection<InsertCipherNameToken> createInsertColumnTokens(final InsertSQLStatementContext insertSQLStatementContext) {
        Optional<InsertColumnsSegment> insertColumnsSegment = insertSQLStatementContext.getSqlStatement().findSQLSegment(InsertColumnsSegment.class);
        if (!insertColumnsSegment.isPresent()) {
            return Collections.emptyList();
        }
        Map<String, String> logicAndCipherColumns = encryptRule.getLogicAndCipherColumns(insertSQLStatementContext.getTablesContext().getSingleTableName());
        Collection<InsertCipherNameToken> result = new LinkedList<>();
        for (ColumnSegment each : insertColumnsSegment.get().getColumns()) {
            if (logicAndCipherColumns.keySet().contains(each.getName())) {
                result.add(new InsertCipherNameToken(each.getStartIndex(), each.getStopIndex(), logicAndCipherColumns.get(each.getName())));
            }
        }
        return result;
    }
}

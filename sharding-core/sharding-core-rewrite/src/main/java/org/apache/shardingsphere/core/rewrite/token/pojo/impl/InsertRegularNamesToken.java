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

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.core.rewrite.token.pojo.Attachable;
import org.apache.shardingsphere.core.rewrite.token.pojo.SQLToken;

import java.util.List;

/**
 * Insert regular names token.
 *
 * @author panjuan
 */
public final class InsertRegularNamesToken extends SQLToken implements Attachable {
    
    @Getter
    private final List<String> columns;
    
    @Setter
    private boolean isToAppendCloseParenthesis;
    
    public InsertRegularNamesToken(final int startIndex, final List<String> columns, final boolean isToAppendCloseParenthesis) {
        super(startIndex);
        this.columns = columns;
        this.isToAppendCloseParenthesis = isToAppendCloseParenthesis;
    }
    
    @Override
    public String toString() {
        if (columns.isEmpty()) {
            return "";
        }
        return String.format(isToAppendCloseParenthesis ? "(%s)" : "(%s", Joiner.on(", ").join(columns));
    }
}

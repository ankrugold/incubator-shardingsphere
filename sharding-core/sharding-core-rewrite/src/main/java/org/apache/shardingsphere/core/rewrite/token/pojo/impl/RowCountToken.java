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
import org.apache.shardingsphere.core.rewrite.token.pojo.SQLToken;
import org.apache.shardingsphere.core.rewrite.token.pojo.Substitutable;

/**
 * Row count token.
 *
 * @author zhangliang
 * @author panjuan
 */
public final class RowCountToken extends SQLToken implements Substitutable {
    
    @Getter
    private final int stopIndex;
    
    private final long revisedRowCount;
    
    public RowCountToken(final int startIndex, final int stopIndex, final long revisedRowCount) {
        super(startIndex);
        this.stopIndex = stopIndex;
        this.revisedRowCount = revisedRowCount;
    }
    
    @Override
    public String toString() {
        return String.valueOf(revisedRowCount);
    }
}

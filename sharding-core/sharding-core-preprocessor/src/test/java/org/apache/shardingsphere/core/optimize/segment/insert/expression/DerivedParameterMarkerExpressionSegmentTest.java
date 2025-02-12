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

package org.apache.shardingsphere.core.optimize.segment.insert.expression;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public final class DerivedParameterMarkerExpressionSegmentTest {
    
    @Test
    public void assertInstanceConstructedOk() {
        int parameterMarkerIndex = 10;
        String type = "type";
        DerivedParameterMarkerExpressionSegment derivedParameterMarkerExpressionSegment = new DerivedParameterMarkerExpressionSegment(parameterMarkerIndex,type);
        assertThat(derivedParameterMarkerExpressionSegment.getType(), is(type));
        assertThat(derivedParameterMarkerExpressionSegment.getStartIndex(), is(0));
        assertThat(derivedParameterMarkerExpressionSegment.getStopIndex(), is(0));
        assertThat(derivedParameterMarkerExpressionSegment.getParameterMarkerIndex(), is(parameterMarkerIndex));
    }
}

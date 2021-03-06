/*
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.quantummaid.mapmaid.specs.examples.system.mode;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.function.Consumer;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FixedExampleMode implements ExampleMode {
    private final Consumer<MapMaidBuilder> fix;

    public static ExampleMode fixed(final Consumer<MapMaidBuilder> fix) {
        return new FixedExampleMode(fix);
    }

    @Override
    public MapMaid provideMapMaid(final ResolvedType type) {
        final MapMaidBuilder mapMaidBuilder = aMapMaid()
                .withAdvancedSettings(advancedBuilder ->
                        advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()));
        this.fix.accept(mapMaidBuilder);
        return mapMaidBuilder.build();
    }
}

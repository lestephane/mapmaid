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

package de.quantummaid.mapmaid.builder.injection;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static java.util.Collections.emptyList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FixedInjectionDeserializer implements TypeDeserializer {
    private final FixedInjector<?> injector;

    public static FixedInjectionDeserializer diDeserializer(final FixedInjector<?> injector) {
        return new FixedInjectionDeserializer(injector);
    }

    @Override
    public List<TypeIdentifier> requiredTypes() {
        return emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(final Universal input,
                             final ExceptionTracker exceptionTracker,
                             final Injector injector,
                             final DeserializerCallback callback,
                             final CustomPrimitiveMappings customPrimitiveMappings,
                             final TypeIdentifier typeIdentifier,
                             final DebugInformation debugInformation) {
        return (T) this.injector.getInstance();
    }

    @Override
    public String description() {
        return "dependency injection";
    }
}

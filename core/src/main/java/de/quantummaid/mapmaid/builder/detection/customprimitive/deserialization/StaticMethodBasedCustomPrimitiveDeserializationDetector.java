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

package de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByMethodDeserializer;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.reflectmaid.ClassType;
import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StaticMethodBasedCustomPrimitiveDeserializationDetector
        implements CustomPrimitiveDeserializationDetector {
    private final CustomPrimitiveMappings mappings;

    public static CustomPrimitiveDeserializationDetector staticMethodBased(final CustomPrimitiveMappings mappings) {
        validateNotNull(mappings, "mappings");
        return new StaticMethodBasedCustomPrimitiveDeserializationDetector(mappings);
    }

    @Override
    public List<TypeDeserializer> detect(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        return findDeserializerMethod((ClassType) type).stream()
                .map(method -> CustomPrimitiveByMethodDeserializer.createDeserializer(type, method))
                .collect(toList());
    }

    private List<ResolvedMethod> findDeserializerMethod(final ClassType type) {
        return type.methods().stream()
                .filter(method -> isStatic(method.method().getModifiers()))
                .filter(method -> method.returnType().isPresent())
                .filter(method -> method.returnType().get().equals(type))
                .filter(method -> method.parameters().size() == 1)
                .filter(method -> this.mappings.isPrimitiveType(method.parameters().get(0).type().assignableType()))
                .collect(toList());
    }
}

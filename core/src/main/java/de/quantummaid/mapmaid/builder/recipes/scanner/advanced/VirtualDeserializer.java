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

package de.quantummaid.mapmaid.builder.recipes.scanner.advanced;

import de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;
import de.quantummaid.reflectmaid.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

import static de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields.deserializationFields;
import static de.quantummaid.mapmaid.shared.identifier.RealTypeIdentifier.realTypeIdentifier;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class VirtualDeserializer implements SerializedObjectDeserializer {
    private final ResolvedMethod method;
    private final DeserializationFields deserializationFields;

    public static SerializedObjectDeserializer virtualDeserializerFor(final ResolvedMethod method) {
        final Map<String, TypeIdentifier> fieldMap = method.parameters().stream()
                .collect(toMap(
                        ResolvedParameter::name,
                        parameter -> realTypeIdentifier(parameter.type())
                ));
        final DeserializationFields deserializationFields = deserializationFields(fieldMap);
        return new VirtualDeserializer(method, deserializationFields);
    }

    @Override
    public DeserializationFields fields() {
        return this.deserializationFields;
    }

    @Override
    public Object deserialize(final Map<String, Object> elements) {
        return elements;
    }

    @Override
    public String description() {
        return format("virtual deserializer for the invocation of '%s'", this.method.describe());
    }
}
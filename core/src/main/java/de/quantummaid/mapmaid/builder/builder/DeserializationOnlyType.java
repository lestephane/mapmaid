/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.builder;

import de.quantummaid.mapmaid.builder.builder.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeserializationOnlyType<T> implements CustomType<T> {
    private final TypeDeserializer deserializer;

    static <T> DeserializationOnlyType<T> customPrimitive(final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(deserializer);
    }

    static <T> DeserializationOnlyType<T> stringBasedCustomPrimitive(final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return createCustomPrimitive(deserializer, String.class);
    }

    static <T> DeserializationOnlyType<T> intBasedCustomPrimitive(final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(deserializer, Integer.class);
    }

    static <T> DeserializationOnlyType<T> floatBasedCustomPrimitive(final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return createCustomPrimitive(deserializer, Float.class);
    }

    static <T> DeserializationOnlyType<T> doubleBasedCustomPrimitive(final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return createCustomPrimitive(deserializer, Double.class);
    }

    static <T> DeserializationOnlyType<T> booleanBasedCustomPrimitive(final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return createCustomPrimitive(deserializer, Boolean.class);
    }

    private static <T, B> DeserializationOnlyType<T> createCustomPrimitive(final CustomCustomPrimitiveDeserializer<T, B> deserializer,
                                                                           final Class<B> baseType) {
        final TypeDeserializer typeDeserializer = deserializer.toTypeDeserializer(baseType);
        return new DeserializationOnlyType<>(typeDeserializer);
    }

    @Override
    public Optional<TypeSerializer> serializer() {
        return Optional.empty();
    }

    @Override
    public Optional<TypeDeserializer> deserializer() {
        return Optional.of(this.deserializer);
    }
}

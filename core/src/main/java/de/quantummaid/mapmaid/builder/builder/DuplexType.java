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
import de.quantummaid.mapmaid.builder.builder.customprimitive.CustomCustomPrimitiveSerializer;
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
public final class DuplexType<T> implements CustomType<T> {
    private final TypeSerializer serializer;
    private final TypeDeserializer deserializer;

    public static <T> DuplexType<T> customPrimitive(final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                    final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(serializer, deserializer);
    }

    public static <T> DuplexType<T> stringBasedCustomPrimitive(final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                               final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return createCustomPrimitive(serializer, deserializer, String.class);
    }

    public static <T> DuplexType<T> intBasedCustomPrimitive(final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(serializer, deserializer, Integer.class);
    }

    public static <T> DuplexType<T> floatBasedCustomPrimitive(final CustomCustomPrimitiveSerializer<T, Float> serializer,
                                                              final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return createCustomPrimitive(serializer, deserializer, Float.class);
    }

    public static <T> DuplexType<T> doubleBasedCustomPrimitive(final CustomCustomPrimitiveSerializer<T, Double> serializer,
                                                               final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return createCustomPrimitive(serializer, deserializer, Double.class);
    }

    public static <T> DuplexType<T> booleanBasedCustomPrimitive(final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
                                                                final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return createCustomPrimitive(serializer, deserializer, Boolean.class);
    }

    private static <T, B> DuplexType<T> createCustomPrimitive(final CustomCustomPrimitiveSerializer<T, B> serializer,
                                                              final CustomCustomPrimitiveDeserializer<T, B> deserializer,
                                                              final Class<B> baseType) {
        final TypeSerializer typeSerializer = serializer.toTypeSerializer(baseType);
        final TypeDeserializer typeDeserializer = deserializer.toTypeDeserializer(baseType);
        return new DuplexType<>(typeSerializer, typeDeserializer);
    }

    @Override
    public Optional<TypeDeserializer> deserializer() {
        return Optional.of(this.deserializer);
    }

    @Override
    public Optional<TypeSerializer> serializer() {
        return Optional.of(this.serializer);
    }
}

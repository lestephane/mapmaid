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

package de.quantummaid.mapmaid.builder.customtypes;

import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionDeserializer;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionFactory;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.Builder00;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionDeserializer.inlinedCollectionDeserializer;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.Builder00.serializedObjectBuilder00;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class DeserializationOnlyType<T> implements CustomType<T> {
    private final TypeIdentifier type;
    private final TypeDeserializer deserializer;

    public static <T> Builder00<T> serializedObject(final Class<T> type) {
        return serializedObject(genericType(type));
    }

    public static <T> Builder00<T> serializedObject(final GenericType<T> type) {
        return serializedObjectBuilder00(type);
    }

    public static <T> DeserializationOnlyType<T> customPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return customPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> customPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(type, deserializer);
    }

    public static <T> DeserializationOnlyType<T> stringBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> stringBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return createCustomPrimitive(type, deserializer, String.class);
    }

    public static <T> DeserializationOnlyType<T> longBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return longBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> longBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return createCustomPrimitive(type, deserializer, Long.class);
    }

    public static <T> DeserializationOnlyType<T> intBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return intBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> intBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(type, deserializer, Integer.class);
    }

    public static <T> DeserializationOnlyType<T> shortBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return shortBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> shortBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return createCustomPrimitive(type, deserializer, Short.class);
    }

    public static <T> DeserializationOnlyType<T> byteBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return byteBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> byteBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return createCustomPrimitive(type, deserializer, Byte.class);
    }

    public static <T> DeserializationOnlyType<T> floatBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return floatBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> floatBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return createCustomPrimitive(type, deserializer, Float.class);
    }

    public static <T> DeserializationOnlyType<T> doubleBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return doubleBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> doubleBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return createCustomPrimitive(type, deserializer, Double.class);
    }

    public static <T> DeserializationOnlyType<T> booleanBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return booleanBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> booleanBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return createCustomPrimitive(type, deserializer, Boolean.class);
    }

    public static <C, T> DeserializationOnlyType<C> inlinedCollection(
            final Class<C> collectionType,
            final Class<T> contentType,
            final InlinedCollectionFactory<C, T> collectionFactory) {
        final GenericType<C> collectionTypeIdentifier = genericType(collectionType);
        final GenericType<T> contentTypeIdentifier = genericType(contentType);
        return inlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, collectionFactory);
    }

    public static <C, T> DeserializationOnlyType<C> inlinedCollection(
            final GenericType<C> collectionType,
            final GenericType<T> contentType,
            final InlinedCollectionFactory<C, T> collectionFactory) {
        final TypeIdentifier collectionTypeIdentifier = typeIdentifierFor(collectionType);
        final TypeIdentifier contentTypeIdentifier = typeIdentifierFor(contentType);
        return inlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, collectionFactory);
    }

    @SuppressWarnings("unchecked")
    public static <C> DeserializationOnlyType<C> inlinedCollection(
            final TypeIdentifier collectionType,
            final TypeIdentifier contentType,
            final InlinedCollectionFactory<?, ?> collectionFactory) {
        final InlinedCollectionDeserializer deserializer =
                inlinedCollectionDeserializer(
                        contentType,
                        (InlinedCollectionFactory<Object, Object>) collectionFactory
                );
        return deserializationOnlyType(collectionType, deserializer);
    }

    public static <T> DeserializationOnlyType<T> deserializationOnlyType(
            final TypeIdentifier type,
            final TypeDeserializer deserializer) {
        validateNotNull(type, "type");
        validateNotNull(deserializer, "deserializer");
        return new DeserializationOnlyType<>(type, deserializer);
    }

    private static <T, B> DeserializationOnlyType<T> createCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, B> deserializer,
            final Class<B> baseType) {
        final TypeDeserializer typeDeserializer = deserializer.toTypeDeserializer(baseType);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return new DeserializationOnlyType<>(typeIdentifier, typeDeserializer);
    }

    @Override
    public TypeIdentifier type() {
        return this.type;
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

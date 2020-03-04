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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldInstantiation;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.customprimitive.CustomPrimitiveSymmetryBuilder;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Filters;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Preferences;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.tiebreaker.TieBreaker;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.EquivalenceClass;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SerializedObjectOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SymmetryBuilder;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.combine;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers.serializersAndDeserializers;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.Picker.*;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.customprimitive.CustomPrimitiveSymmetryBuilder.customPrimitiveSymmetryBuilder;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SerializedObjectOptions.serializedObjectOptions;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SymmetryBuilder.symmetryBuilder;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NormalDisambiguator implements Disambiguator {
    private final Preferences<TypeDeserializer> customPrimitiveDeserializerPreferences;
    private final Preferences<TypeSerializer> customPrimitiveSerializerPreferences;
    private final Preferences<TypeDeserializer> serializedObjectDeserializerPreferences;

    private final Filters<SerializationField> serializationFieldFilters;
    private final Preferences<SerializationField> postSymmetrySerializationFieldPreferences;

    private final TieBreaker tieBreaker;

    public static NormalDisambiguator normalDisambiguator(final Preferences<TypeDeserializer> customPrimitiveDeserializerPreferences,
                                                          final Preferences<TypeSerializer> customPrimitiveSerializerPreferences,
                                                          final Preferences<TypeDeserializer> serializedObjectPreferences,
                                                          final Filters<SerializationField> serializationFieldFilters,
                                                          final Preferences<SerializationField> postSymmetrySerializationFieldPreferences,
                                                          final TieBreaker tieBreaker) {
        return new NormalDisambiguator(
                customPrimitiveDeserializerPreferences,
                customPrimitiveSerializerPreferences,
                serializedObjectPreferences,
                serializationFieldFilters,
                postSymmetrySerializationFieldPreferences,
                tieBreaker
        );
    }

    @Override
    public DetectionResult<DisambiguationResult> disambiguate(final ResolvedType type,
                                                              final SerializedObjectOptions serializedObjectOptions,
                                                              final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
                                                              final ScanInformationBuilder scanInformationBuilder) {
        if (type.assignableType().getPackageName().startsWith("java.")) {
            return failure("Native java classes cannot be detected");
        }

        final SerializedObjectOptions filteredSerializedObjectOptions = filterSerializedObjectOptions(
                serializedObjectOptions, scanInformationBuilder);

        final SerializersAndDeserializers preferredCustomPrimitiveSerializersAndDeserializers = filterCustomPrimitiveOptions(
                customPrimitiveSerializersAndDeserializers, scanInformationBuilder);

        if (preferredCustomPrimitiveSerializersAndDeserializers.serializationOnly()) {
            return serializationOnly(preferredCustomPrimitiveSerializersAndDeserializers, filteredSerializedObjectOptions, scanInformationBuilder);
        }

        if (preferredCustomPrimitiveSerializersAndDeserializers.deserializationOnly()) {
            return deserializationOnly(preferredCustomPrimitiveSerializersAndDeserializers, filteredSerializedObjectOptions, scanInformationBuilder);
        }

        return duplex(type, preferredCustomPrimitiveSerializersAndDeserializers, filteredSerializedObjectOptions, scanInformationBuilder);
    }

    private DetectionResult<DisambiguationResult> serializationOnly(final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
                                                                    final SerializedObjectOptions serializedObjectOptions,
                                                                    final ScanInformationBuilder scanInformationBuilder) {
        final List<TypeSerializer> customPrimitiveSerializers = customPrimitiveSerializersAndDeserializers.serializers();
        final DetectionResult<TypeSerializer> customPrimitiveSerializer = oneOrNone(customPrimitiveSerializers, TypeSerializer::description)
                .orElseGet(() -> failure("No serializers to choose from"));

        final DetectionResult<TypeSerializer> serializedObjectSerializer = serializedObjectOptions.determineSerializer(
                this.postSymmetrySerializationFieldPreferences, scanInformationBuilder);

        return this.tieBreaker.breakTieForSerializationOnly(customPrimitiveSerializer, serializedObjectSerializer, scanInformationBuilder)
                .map(DisambiguationResult::serializationOnlyResult);
    }

    private DetectionResult<DisambiguationResult> deserializationOnly(final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
                                                                      final SerializedObjectOptions serializedObjectOptions,
                                                                      final ScanInformationBuilder scanInformationBuilder) {
        final DetectionResult<TypeDeserializer> customPrimitiveDeserializer = pickDeserializer(customPrimitiveSerializersAndDeserializers.deserializers());
        final DetectionResult<TypeDeserializer> serializedObjectDeserializer = pickDeserializer(serializedObjectOptions.deserializers());
        return this.tieBreaker.breakTieForDeserializationOnly(customPrimitiveDeserializer, serializedObjectDeserializer, scanInformationBuilder)
                .map(DisambiguationResult::deserializationOnlyResult);
    }

    private DetectionResult<DisambiguationResult> duplex(final ResolvedType type,
                                                         final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
                                                         final SerializedObjectOptions serializedObjectOptions,
                                                         final ScanInformationBuilder scanInformationBuilder) {
        final CustomPrimitiveSymmetryBuilder customPrimitiveSymmetryBuilder = customPrimitiveSymmetryBuilder();
        customPrimitiveSerializersAndDeserializers.serializers()
                .forEach(serializer -> customPrimitiveSymmetryBuilder.addSerializer((CustomPrimitiveSerializer) serializer));
        customPrimitiveSerializersAndDeserializers.deserializers()
                .forEach(deserializer -> customPrimitiveSymmetryBuilder.addDeserializer((CustomPrimitiveDeserializer) deserializer));
        final Optional<SerializersAndDeserializers> customPrimitiveResult = customPrimitiveSymmetryBuilder.determineGreatestCommonFields();

        if (customPrimitiveResult.isPresent()) {
            return symmetricCustomPrimitive(customPrimitiveResult.get(), scanInformationBuilder);
        }
        return symmetricSerializedObject(type, serializedObjectOptions, scanInformationBuilder);
    }

    private DetectionResult<DisambiguationResult> symmetricCustomPrimitive(final SerializersAndDeserializers serializersAndDeserializers,
                                                                           final ScanInformationBuilder scanInformationBuilder) {
        final DetectionResult<TypeSerializer> serializer = pickSerializer(serializersAndDeserializers);
        if (!serializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(),
                    format("less priority than %s", serializer.result().description()));
        }
        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(serializersAndDeserializers.deserializers());
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(),
                    format("less priority than %s", deserializer.result().description()));
        }
        return combine(serializer, deserializer, DisambiguationResult::duplexResult);
    }

    private DetectionResult<DisambiguationResult> symmetricSerializedObject(final ResolvedType type,
                                                                            final SerializedObjectOptions serializedObjectOptions,
                                                                            final ScanInformationBuilder scanInformationBuilder) {
        final SymmetryBuilder symmetryBuilder = symmetryBuilder();
        final List<TypeDeserializer> deserializers = serializedObjectOptions.deserializers();
        deserializers.forEach(symmetryBuilder::addDeserializer);
        symmetryBuilder.addSerializer(serializedObjectOptions.serializationFieldOptions());

        final DetectionResult<EquivalenceClass> symmetric = symmetryBuilder.determineGreatestCommonFields();
        if (symmetric.isFailure()) {
            return failure(format("Failed to detect %s:%n%s", type.description(), symmetric.reasonForFailure()));
        }

        final EquivalenceClass symmetricResult = symmetric.result();

        final SerializationFieldInstantiation serializationFields = symmetricResult.serializationFields();
        final DetectionResult<TypeSerializer> serializer = serializationFields.instantiate(
                this.postSymmetrySerializationFieldPreferences, scanInformationBuilder);
        if (!serializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(), "insufficient symmetry");
        }

        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(symmetricResult.deserializers());
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(), "insufficient symmetry");
        }

        return combine(serializer, deserializer, DisambiguationResult::duplexResult);
    }

    private SerializersAndDeserializers filterCustomPrimitiveOptions(final SerializersAndDeserializers serializersAndDeserializers,
                                                                     final ScanInformationBuilder scanInformationBuilder) {

        final List<TypeSerializer> preferredCustomPrimitiveSerializers;
        if (serializersAndDeserializers.serializers() != null) {
            final List<TypeSerializer> customPrimitveSerializers = serializersAndDeserializers.serializers();
            preferredCustomPrimitiveSerializers = this.customPrimitiveSerializerPreferences.preferred(
                    customPrimitveSerializers, scanInformationBuilder::ignoreSerializer);
        } else {
            preferredCustomPrimitiveSerializers = null;
        }

        final List<TypeDeserializer> preferredCustomPrimitiveDeserializers;
        if (serializersAndDeserializers.deserializers() != null) {
            final List<TypeDeserializer> customPrimitiveDeserializers = serializersAndDeserializers.deserializers();
            preferredCustomPrimitiveDeserializers = this.customPrimitiveDeserializerPreferences.preferred(
                    customPrimitiveDeserializers, scanInformationBuilder::ignoreDeserializer);
        } else {
            preferredCustomPrimitiveDeserializers = null;
        }

        return serializersAndDeserializers(preferredCustomPrimitiveSerializers, preferredCustomPrimitiveDeserializers);
    }

    private SerializedObjectOptions filterSerializedObjectOptions(final SerializedObjectOptions serializedObjectOptions,
                                                                  final ScanInformationBuilder scanInformationBuilder) {
        final SerializationFieldOptions serializationFieldOptions = serializedObjectOptions.serializationFieldOptions();
        final SerializationFieldOptions filteredSerialzationFields;

        if (serializationFieldOptions != null) {
            filteredSerialzationFields = serializationFieldOptions.filter(
                    field -> this.serializationFieldFilters.isAllowed(field, scanInformationBuilder::ignoreSerializationField)
            );
        } else {
            filteredSerialzationFields = null;
        }

        final List<TypeDeserializer> filteredSerializedObjectDeserializers;
        if (serializedObjectOptions.deserializers() != null) {
            final List<TypeDeserializer> serializedObjectDeserializers = serializedObjectOptions.deserializers();
            filteredSerializedObjectDeserializers = this.serializedObjectDeserializerPreferences.preferred(
                    serializedObjectDeserializers, scanInformationBuilder::ignoreDeserializer);
        } else {
            filteredSerializedObjectDeserializers = null;
        }
        return serializedObjectOptions(filteredSerialzationFields, filteredSerializedObjectDeserializers);
    }
}

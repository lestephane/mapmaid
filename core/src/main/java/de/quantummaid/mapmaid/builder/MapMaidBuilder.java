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

package de.quantummaid.mapmaid.builder;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.builder.CustomTypesBuilder;
import de.quantummaid.mapmaid.builder.builder.DetectedTypesBuilder;
import de.quantummaid.mapmaid.builder.builder.InjectingBuilder;
import de.quantummaid.mapmaid.builder.builder.ProgrammaticTypeBuilder;
import de.quantummaid.mapmaid.builder.conventional.ConventionalDetectors;
import de.quantummaid.mapmaid.builder.customtypes.CustomType;
import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult;
import de.quantummaid.mapmaid.builder.resolving.processing.Processor;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.deserialization.Deserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.*;
import de.quantummaid.mapmaid.mapper.marshalling.registry.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.registry.UnmarshallerRegistry;
import de.quantummaid.mapmaid.mapper.serialization.Serializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.MapMaid.mapMaid;
import static de.quantummaid.mapmaid.builder.AdvancedBuilder.advancedBuilder;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.*;
import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.injection.InjectionSerializer.injectionSerializer;
import static de.quantummaid.mapmaid.builder.resolving.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.processing.Processor.processor;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddDeserializationSignal.addDeserialization;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddManualDeserializerSignal.addManualDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddManualSerializerSignal.addManualSerializer;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddSerializationSignal.addSerialization;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.builder.resolving.states.injecting.InjectedDefinition.injectedDefinition;
import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.debug.Reason.manuallyAdded;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
import static de.quantummaid.mapmaid.mapper.deserialization.Deserializer.theDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.Serializer.serializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer.polymorphicDeserializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicSerializer.polymorphicSerializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicUtils.nameToIdentifier;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static java.lang.String.format;
import static java.util.Arrays.asList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class MapMaidBuilder implements
        DetectedTypesBuilder,
        InjectingBuilder,
        ProgrammaticTypeBuilder,
        CustomTypesBuilder {
    private final List<ManuallyAddedState> manuallyAddedStates = new ArrayList<>();
    private final SimpleDetector detector = ConventionalDetectors.conventionalDetector();
    private final Processor processor = processor();
    private final AdvancedBuilder advancedBuilder = advancedBuilder();
    private final List<Recipe> recipes = smallList();
    private final ValidationMappings validationMappings = ValidationMappings.empty();
    private final ValidationErrorsMapping validationErrorsMapping = validationErrors -> {
        throw AggregatedValidationException.fromList(validationErrors);
    };

    public static MapMaidBuilder mapMaidBuilder() {
        return new MapMaidBuilder();
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "unchecked", "rawtypes"})
    public final <T> MapMaidBuilder serializingSubtypes(final Class<T> superType,
                                                        final Class<? extends T>... subTypes) {
        final GenericType<T> genericSuperType = genericType(superType);
        final GenericType[] genericSubTypes = Arrays.stream(subTypes)
                .map(GenericType::genericType)
                .toArray(GenericType[]::new);
        return serializingSubtypes(genericSuperType, genericSubTypes);
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "unchecked", "rawtypes"})
    public final <T> MapMaidBuilder serializingSubtypes(final GenericType<T> superType,
                                                        final GenericType<? extends T>... subTypes) {
        final TypeIdentifier superTypeIdentifier = typeIdentifierFor(superType);
        final TypeIdentifier[] subTypeIdentifiers = Arrays.stream(subTypes)
                .map(TypeIdentifier::typeIdentifierFor)
                .toArray(TypeIdentifier[]::new);
        return serializingSubtypes(superTypeIdentifier, subTypeIdentifiers);
    }

    public MapMaidBuilder serializingSubtypes(final TypeIdentifier superType,
                                              final TypeIdentifier... subTypes) {
        return withSubtypes(serialization(), superType, asList(subTypes));
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "unchecked", "rawtypes"})
    public final <T> MapMaidBuilder deserializingSubtypes(final Class<T> superType,
                                                          final Class<? extends T>... subTypes) {
        final GenericType<T> genericSuperType = genericType(superType);
        final GenericType[] genericSubTypes = Arrays.stream(subTypes)
                .map(GenericType::genericType)
                .toArray(GenericType[]::new);
        return deserializingSubtypes(genericSuperType, genericSubTypes);
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "unchecked", "rawtypes"})
    public final <T> MapMaidBuilder deserializingSubtypes(final GenericType<T> superType,
                                                          final GenericType<? extends T>... subTypes) {
        final TypeIdentifier superTypeIdentifier = typeIdentifierFor(superType);
        final TypeIdentifier[] subTypeIdentifiers = Arrays.stream(subTypes)
                .map(TypeIdentifier::typeIdentifierFor)
                .toArray(TypeIdentifier[]::new);
        return deserializingSubtypes(superTypeIdentifier, subTypeIdentifiers);
    }

    public MapMaidBuilder deserializingSubtypes(final TypeIdentifier superType,
                                                final TypeIdentifier... subTypes) {
        return withSubtypes(deserialization(), superType, asList(subTypes));
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "unchecked", "rawtypes"})
    public final <T> MapMaidBuilder serializingAndDeserializingSubtypes(final Class<T> superType,
                                                                        final Class<? extends T>... subTypes) {
        final GenericType<T> genericSuperType = genericType(superType);
        final GenericType[] genericSubTypes = Arrays.stream(subTypes)
                .map(GenericType::genericType)
                .toArray(GenericType[]::new);
        return serializingAndDeserializingSubtypes(genericSuperType, genericSubTypes);
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "unchecked", "rawtypes"})
    public final <T> MapMaidBuilder serializingAndDeserializingSubtypes(final GenericType<T> superType,
                                                                        final GenericType<? extends T>... subTypes) {
        final TypeIdentifier superTypeIdentifier = typeIdentifierFor(superType);
        final TypeIdentifier[] subTypeIdentifiers = Arrays.stream(subTypes)
                .map(TypeIdentifier::typeIdentifierFor)
                .toArray(TypeIdentifier[]::new);
        return serializingAndDeserializingSubtypes(superTypeIdentifier, subTypeIdentifiers);
    }

    public MapMaidBuilder serializingAndDeserializingSubtypes(final TypeIdentifier superType,
                                                              final TypeIdentifier... subTypes) {
        return withSubtypes(duplex(), superType, asList(subTypes));
    }

    public MapMaidBuilder withSubtypes(final RequiredCapabilities capabilities,
                                       final TypeIdentifier superType,
                                       final List<TypeIdentifier> subTypes) {
        manuallyAddedStates.add(configuration -> {
            final BiMap<String, TypeIdentifier> nameToType = nameToIdentifier(subTypes, configuration);
            final String typeIdentifierKey = configuration.getTypeIdentifierKey();
            final Context context = emptyContext(this.processor::dispatch, superType);
            final StatefulDefinition statefulDefinition = unreasoned(context);
            this.processor.addState(statefulDefinition);
            if (capabilities.hasSerialization()) {
                final TypeSerializer serializer = polymorphicSerializer(superType, nameToType, typeIdentifierKey);
                processor.dispatch(addManualSerializer(superType, serializer));
                processor.dispatch(addSerialization(superType, manuallyAdded()));
            }
            if (capabilities.hasDeserialization()) {
                final PolymorphicDeserializer deserializer = polymorphicDeserializer(superType, nameToType, typeIdentifierKey);
                processor.dispatch(addManualDeserializer(superType, deserializer));
                processor.dispatch(addDeserialization(superType, manuallyAdded()));
            }
        });
        return this;
    }

    @Override
    public MapMaidBuilder injecting(final TypeIdentifier typeIdentifier, final TypeDeserializer deserializer) {
        final Context context = emptyContext(this.processor::dispatch, typeIdentifier);
        final TypeSerializer serializer = injectionSerializer(typeIdentifier);
        context.setSerializer(serializer);
        context.setDeserializer(deserializer);
        final StatefulDefinition statefulDefinition = injectedDefinition(context);
        this.processor.addState(statefulDefinition);
        this.processor.dispatch(addSerialization(typeIdentifier, manuallyAdded()));
        this.processor.dispatch(addDeserialization(typeIdentifier, manuallyAdded()));
        return this;
    }

    @Override
    public MapMaidBuilder withType(final GenericType<?> type,
                                   final RequiredCapabilities capabilities) {
        return withType(type, capabilities, manuallyAdded());
    }

    @Override
    public MapMaidBuilder withType(final GenericType<?> type,
                                   final RequiredCapabilities capabilities,
                                   final Reason reason) {
        validateNotNull(type, "type");
        validateNotNull(capabilities, "capabilities");
        validateNotNull(reason, "reason");
        manuallyAddedStates.add(configuration -> {
            final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
            if (capabilities.hasSerialization()) {
                this.processor.dispatch(addSerialization(typeIdentifier, reason));
            }
            if (capabilities.hasDeserialization()) {
                this.processor.dispatch(addDeserialization(typeIdentifier, reason));
            }
        });
        return this;
    }

    @Override
    public <T> MapMaidBuilder withCustomType(final RequiredCapabilities capabilities,
                                             final CustomType<T> customType) {
        validateNotNull(capabilities, "capabilities");
        validateNotNull(customType, "customType");
        manuallyAddedStates.add(configuration -> {
            final TypeIdentifier typeIdentifier = customType.type();
            final Context context = emptyContext(this.processor::dispatch, typeIdentifier);
            final StatefulDefinition statefulDefinition = unreasoned(context);
            this.processor.addState(statefulDefinition);
            if (capabilities.hasSerialization()) {
                final Optional<TypeSerializer> serializer = customType.serializer();
                if (serializer.isEmpty()) {
                    throw new IllegalArgumentException(format(
                            "serializer is missing for type '%s'", typeIdentifier.description()));
                }
                this.processor.dispatch(addManualSerializer(typeIdentifier, serializer.get()));
                this.processor.dispatch(addSerialization(typeIdentifier, manuallyAdded()));
            }
            if (capabilities.hasDeserialization()) {
                final Optional<TypeDeserializer> deserializer = customType.deserializer();
                if (deserializer.isEmpty()) {
                    throw new IllegalArgumentException(format("deserializer is missing for type '%s'",
                            typeIdentifier.description()));
                }
                this.processor.dispatch(addManualDeserializer(typeIdentifier, deserializer.get()));
                this.processor.dispatch(addDeserialization(typeIdentifier, manuallyAdded()));
            }
        });
        return this;
    }

    public MapMaidBuilder usingRecipe(final Recipe recipe) {
        this.recipes.add(recipe);
        return this;
    }

    public <T extends Throwable> MapMaidBuilder withExceptionIndicatingValidationError(
            final Class<T> exceptionIndicatingValidationError) {
        return this.withExceptionIndicatingValidationError(
                exceptionIndicatingValidationError,
                (exception, propertyPath) -> new ValidationError(exception.getMessage(), propertyPath));
    }

    @SuppressWarnings("unchecked")
    public <T extends Throwable> MapMaidBuilder withExceptionIndicatingValidationError(
            final Class<T> exceptionIndicatingValidationError,
            final ExceptionMappingWithPropertyPath<T> exceptionMapping) {
        this.validationMappings.putOneToOne(exceptionIndicatingValidationError,
                (ExceptionMappingWithPropertyPath<Throwable>) exceptionMapping);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Throwable> MapMaidBuilder withExceptionIndicatingMultipleValidationErrors(
            final Class<T> exceptionType,
            final ExceptionMappingList<T> mapping) {
        validateNotNull(exceptionType, "exceptionType");
        validateNotNull(mapping, "mapping");
        this.validationMappings.putOneToMany(exceptionType, (ExceptionMappingList<Throwable>) mapping);
        return this;
    }

    public MapMaidBuilder withAdvancedSettings(final Consumer<AdvancedBuilder> configurator) {
        configurator.accept(this.advancedBuilder);
        return this;
    }

    public MapMaid build() {
        this.recipes.forEach(Recipe::init);
        this.recipes.forEach(recipe -> recipe.cook(this));

        final MapMaidConfiguration mapMaidConfiguration = advancedBuilder.mapMaidConfiguration();
        manuallyAddedStates.forEach(manuallyAddedState -> manuallyAddedState.addState(mapMaidConfiguration));

        final Disambiguators disambiguators = this.advancedBuilder.buildDisambiguators();
        final Map<TypeIdentifier, CollectionResult> result = this.processor.collect(
                this.detector,
                disambiguators,
                mapMaidConfiguration
        );

        final Map<TypeIdentifier, Definition> definitionsMap = new HashMap<>(result.size());
        final Map<TypeIdentifier, ScanInformationBuilder> scanInformationMap = new HashMap<>(result.size());
        result.forEach((type, collectionResult) -> {
            definitionsMap.put(type, collectionResult.definition());
            scanInformationMap.put(type, collectionResult.scanInformation());
        });

        final DebugInformation debugInformation = debugInformation(scanInformationMap);
        final Definitions definitions = definitions(definitionsMap, debugInformation);

        final MarshallerRegistry marshallerRegistry = this.advancedBuilder.buildMarshallerRegistry();
        final Serializer serializer = serializer(
                marshallerRegistry,
                definitions,
                CUSTOM_PRIMITIVE_MAPPINGS,
                debugInformation
        );

        final UnmarshallerRegistry unmarshallerRegistry = this.advancedBuilder.buildUnmarshallerRegistry();
        final Deserializer deserializer = theDeserializer(
                unmarshallerRegistry,
                definitions,
                CUSTOM_PRIMITIVE_MAPPINGS,
                this.validationMappings,
                this.validationErrorsMapping,
                debugInformation
        );
        return mapMaid(serializer, deserializer, debugInformation);
    }
}

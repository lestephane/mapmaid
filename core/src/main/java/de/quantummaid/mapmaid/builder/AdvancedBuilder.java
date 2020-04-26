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

import de.quantummaid.mapmaid.builder.autoload.Autoloadable;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.DisambiguatorBuilder;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.NormalDisambiguator;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.quantummaid.mapmaid.Collection.smallMap;
import static de.quantummaid.mapmaid.builder.autoload.ActualAutoloadable.autoloadIfClassPresent;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators.disambiguators;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.DisambiguatorBuilder.defaultDisambiguatorBuilder;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry.marshallerRegistry;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdvancedBuilder {
    private static final List<Autoloadable<MarshallerAndUnmarshaller>> AUTOLOADABLE_MARSHALLERS = List.of(
            autoloadIfClassPresent("de.quantummaid.mapmaid.jackson.JacksonJsonMarshaller"),
            autoloadIfClassPresent("de.quantummaid.mapmaid.jackson.JacksonXmlMarshaller"),
            autoloadIfClassPresent("de.quantummaid.mapmaid.jackson.JacksonYamlMarshaller")
    );

    private Map<MarshallingType, Marshaller> marshallerMap = smallMap();
    private Map<MarshallingType, Unmarshaller> unmarshallerMap = smallMap();
    private final DisambiguatorBuilder defaultDisambiguatorBuilder = defaultDisambiguatorBuilder();
    private boolean autoloadMarshallers = true;
    private List<MarshallerAndUnmarshaller> autoloadedMarshallers = null;

    public static AdvancedBuilder advancedBuilder() {
        return new AdvancedBuilder();
    }

    public AdvancedBuilder withPreferredCustomPrimitiveFactoryName(final String name) {
        this.defaultDisambiguatorBuilder.setPreferredCustomPrimitiveFactoryName(name);
        return this;
    }

    public AdvancedBuilder withPreferredCustomPrimitiveSerializationMethodName(final String name) {
        this.defaultDisambiguatorBuilder.setPreferredCustomPrimitiveSerializationMethodName(name);
        return this;
    }

    public AdvancedBuilder withPreferredSerializedObjectFactoryName(final String name) {
        this.defaultDisambiguatorBuilder.setPreferredSerializedObjectFactoryName(name);
        return this;
    }

    public AdvancedBuilder doNotAutoloadMarshallers() {
        this.autoloadMarshallers = false;
        return this;
    }

    public AdvancedBuilder usingMarshaller(final MarshallerAndUnmarshaller marshallerAndUnmarshaller) {
        final MarshallingType marshallingType = marshallerAndUnmarshaller.marshallingType();
        final Marshaller marshaller = marshallerAndUnmarshaller.marshaller();
        final Unmarshaller unmarshaller = marshallerAndUnmarshaller.unmarshaller();
        return usingMarshaller(marshallingType, marshaller, unmarshaller);
    }

    public AdvancedBuilder usingMarshaller(final MarshallingType marshallingType,
                                           final Marshaller marshaller,
                                           final Unmarshaller unmarshaller) {
        validateNotNull(marshaller, "marshaller");
        validateNotNull(unmarshaller, "unmarshaller");
        validateNotNull(marshallingType, "marshallingType");
        this.marshallerMap.put(marshallingType, marshaller);
        this.unmarshallerMap.put(marshallingType, unmarshaller);
        return doNotAutoloadMarshallers();
    }

    public AdvancedBuilder usingMarshaller(final Map<MarshallingType, Marshaller> marshallerMap,
                                           final Map<MarshallingType, Unmarshaller> unmarshallerMap) {
        this.marshallerMap = new HashMap<>(marshallerMap);
        this.unmarshallerMap = new HashMap<>(unmarshallerMap);
        return doNotAutoloadMarshallers();
    }

    public AdvancedBuilder usingJsonMarshaller(final Marshaller marshaller, final Unmarshaller unmarshaller) {
        validateNotNull(marshaller, "jsonMarshaller");
        validateNotNull(unmarshaller, "jsonUnmarshaller");
        return usingMarshaller(MarshallingType.JSON, marshaller, unmarshaller);
    }

    public AdvancedBuilder usingYamlMarshaller(final Marshaller marshaller, final Unmarshaller unmarshaller) {
        validateNotNull(marshaller, "yamlMarshaller");
        validateNotNull(unmarshaller, "yamlUnmarshaller");
        return usingMarshaller(MarshallingType.YAML, marshaller, unmarshaller);
    }

    public AdvancedBuilder usingXmlMarshaller(final Marshaller marshaller, final Unmarshaller unmarshaller) {
        validateNotNull(marshaller, "xmlMarshaller");
        validateNotNull(unmarshaller, "xmlUnmarshaller");
        return usingMarshaller(MarshallingType.XML, marshaller, unmarshaller);
    }

    Disambiguators buildDisambiguators() {
        final NormalDisambiguator defaultDisambiguator = this.defaultDisambiguatorBuilder.build();
        final Map<ResolvedType, Disambiguator> specialDisambiguators = smallMap();
        return disambiguators(defaultDisambiguator, specialDisambiguators);
    }

    MarshallerRegistry<Marshaller> buildMarshallerRegistry() {
        if (this.autoloadMarshallers) {
            autoload();
            this.autoloadedMarshallers.forEach(autoloadableMarshaller -> {
                final MarshallingType marshallingType = autoloadableMarshaller.marshallingType();
                final Marshaller marshaller = autoloadableMarshaller.marshaller();
                this.marshallerMap.put(marshallingType, marshaller);
            });
        }
        return marshallerRegistry(this.marshallerMap);
    }

    MarshallerRegistry<Unmarshaller> buildUnmarshallerRegistry() {
        if (this.autoloadMarshallers) {
            autoload();
            this.autoloadedMarshallers.forEach(autoloadableMarshaller -> {
                final MarshallingType marshallingType = autoloadableMarshaller.marshallingType();
                final Unmarshaller unmarshaller = autoloadableMarshaller.unmarshaller();
                this.unmarshallerMap.put(marshallingType, unmarshaller);
            });
        }
        return marshallerRegistry(this.unmarshallerMap);
    }

    private void autoload() {
        if (this.autoloadedMarshallers == null) {
            this.autoloadedMarshallers = AUTOLOADABLE_MARSHALLERS.stream()
                    .map(Autoloadable::autoload)
                    .flatMap(Optional::stream)
                    .collect(toList());
        }
    }
}

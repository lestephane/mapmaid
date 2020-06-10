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

package de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex;

import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Deserializer06;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Query;
import de.quantummaid.reflectmaid.GenericType;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex.Common.createDuplexType;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@RequiredArgsConstructor
public final class SerializedObjectBuilder06<X, A, B, C, D, E, F> {
    private final Builder builder;

    public <G> SerializedObjectBuilder07<X, A, B, C, D, E, F, G> withField(final String name,
                                                                           final Class<G> type,
                                                                           final Query<X, G> query) {
        return withField(name, genericType(type), query);
    }

    @SuppressWarnings("unchecked")
    public <G> SerializedObjectBuilder07<X, A, B, C, D, E, F, G> withField(final String name,
                                                                           final GenericType<G> type,
                                                                           final Query<X, G> query) {
        builder.addDuplexField(type, name, (Query<Object, Object>) query);
        return new SerializedObjectBuilder07<>(builder);
    }

    public DuplexType<X> deserializedUsing(final Deserializer06<X, A, B, C, D, E, F> deserializer) {
        builder.setDeserializer(deserializer);
        return createDuplexType(builder);
    }
}

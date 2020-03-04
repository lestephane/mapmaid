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

package de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only;

import de.quantummaid.mapmaid.builder.GenericType;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;

import static de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType.deserializationOnlyType;

public final class Common {

    private Common() {
    }

    @SuppressWarnings("unchecked")
    static <T> DeserializationOnlyType<T> createDeserializationOnlyType(final Builder builder) {
        final GenericType<?> type = builder.getType();
        final TypeDeserializer deserializer = builder.createDeserializer();
        return (DeserializationOnlyType<T>) deserializationOnlyType(type, deserializer);
    }
}
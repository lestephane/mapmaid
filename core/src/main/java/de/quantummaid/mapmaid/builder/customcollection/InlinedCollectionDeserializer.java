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

package de.quantummaid.mapmaid.builder.customcollection;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.CollectionDeserializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InlinedCollectionDeserializer implements CollectionDeserializer {
    private final TypeIdentifier contentType;
    private final InlinedCollectionFactory<Object, Object> collectionFactory;

    public static InlinedCollectionDeserializer inlinedCollectionDeserializer(
            final TypeIdentifier contentTypeIdentifier,
            final InlinedCollectionFactory<Object, Object> collectionFactory) {
        return new InlinedCollectionDeserializer(contentTypeIdentifier, collectionFactory);
    }

    @Override
    public TypeIdentifier contentType() {
        return this.contentType;
    }

    @Override
    public Object listToCollection(final List<Object> deserializedElements) {
        return this.collectionFactory.create(deserializedElements);
    }

    @Override
    public String description() {
        return "custom collection";
    }
}

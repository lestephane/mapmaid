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

package de.quantummaid.mapmaid.builder.recipes.urlencoded;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ParsedUrlEncoded {
    private final List<KeyValue> keyValues;

    static ParsedUrlEncoded parse(final String string) {
        NotNullValidator.validateNotNull(string, "string");
        final List<KeyValue> keyValues = stream(string.split("&"))
                .filter(s -> !s.isEmpty())
                .map(KeyValue::parse)
                .collect(toList());
        return new ParsedUrlEncoded(keyValues);
    }

    List<KeyElement> directChildren(final Key prefix) {
        return this.keyValues.stream()
                .map(KeyValue::key)
                .filter(prefix::isPrefixTo)
                .map(key -> key.elementAfter(prefix))
                .collect(toList());
    }

    Optional<String> getValue(final Key key) {
        return this.keyValues.stream()
                .filter(keyValue -> keyValue.key().equals(key))
                .map(KeyValue::value)
                .findFirst();
    }
}

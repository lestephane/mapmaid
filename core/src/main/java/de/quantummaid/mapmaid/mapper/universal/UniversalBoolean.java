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

package de.quantummaid.mapmaid.mapper.universal;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.shared.mapping.BooleanFormatException.booleanFormatException;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UniversalBoolean implements UniversalPrimitive {
    private final boolean value;

    public static UniversalBoolean universalBoolean(final boolean value) {
        return new UniversalBoolean(value);
    }

    public static UniversalBoolean universalBooleanFromUniversalString(final UniversalString universalString) {
        final String stringValue = (String) universalString.toNativeJava();
        return universalBooleanFromString(stringValue);
    }

    private static UniversalBoolean universalBooleanFromString(final String stringValue) {
        switch (stringValue) {
            case "true":
                return universalBoolean(true);
            case "false":
                return universalBoolean(false);
            default:
                throw booleanFormatException(stringValue);
        }
    }

    @Override
    public Object toNativeJava() {
        return this.value;
    }

    public Boolean toNativeBoolean() {
        return this.value;
    }
}

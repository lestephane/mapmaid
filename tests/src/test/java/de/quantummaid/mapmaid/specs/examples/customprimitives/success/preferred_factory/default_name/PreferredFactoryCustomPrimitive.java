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

package de.quantummaid.mapmaid.specs.examples.customprimitives.success.preferred_factory.default_name;

import de.quantummaid.mapmaid.specs.examples.system.WrongMethodCalledException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PreferredFactoryCustomPrimitive {
    private final String value;

    public static PreferredFactoryCustomPrimitive factoryA(final String value) {
        throw WrongMethodCalledException.wrongMethodCalledException();
    }

    public static PreferredFactoryCustomPrimitive factoryB(final String value) {
        throw WrongMethodCalledException.wrongMethodCalledException();
    }

    public static PreferredFactoryCustomPrimitive fromStringValue(final String value) {
        return new PreferredFactoryCustomPrimitive(value);
    }

    public static PreferredFactoryCustomPrimitive factoryC(final String value) {
        throw WrongMethodCalledException.wrongMethodCalledException();
    }

    public static PreferredFactoryCustomPrimitive factoryD(final String value) {
        throw WrongMethodCalledException.wrongMethodCalledException();
    }

    public String getValue() {
        return this.value;
    }
}

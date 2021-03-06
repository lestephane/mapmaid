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

package de.quantummaid.mapmaid.specs.examples.customprimitives.success.factory_by_classname.example3;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.specs.examples.system.WrongMethodCalledException.wrongMethodCalledException;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GroupName {
    private final String value;

    public static GroupName groupName(final String value) {
        return new GroupName(value);
    }

    public String stringValue() {
        return this.value;
    }

    public String stringValue(final String encoding) {
        throw wrongMethodCalledException();
    }

    public String lowerCase() {
        throw wrongMethodCalledException();
    }
}

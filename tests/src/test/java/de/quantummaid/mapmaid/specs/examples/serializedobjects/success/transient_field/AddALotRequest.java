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

package de.quantummaid.mapmaid.specs.examples.serializedobjects.success.transient_field;

import de.quantummaid.mapmaid.specs.examples.customprimitives.success.normal.example1.Name;
import de.quantummaid.mapmaid.specs.examples.customprimitives.success.normal.example2.TownName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddALotRequest {
    public final Name name;
    public final transient TownName townNameA;
    final TownName townNameB;
    private final TownName townNameC;

    public static AddALotRequest addALotRequest(final Name name) {
        return new AddALotRequest(name, TownName.townName("a"), TownName.townName("b"), TownName.townName("c"));
    }
}

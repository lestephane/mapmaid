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

package de.quantummaid.mapmaid.specs.examples.customprimitives.conflicting.type_variable_with_different_name;

import de.quantummaid.reflectmaid.GenericType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.builder.customtypes.DuplexType.customPrimitive;
import static de.quantummaid.mapmaid.specs.examples.customprimitives.conflicting.type_variable_with_different_name.Street.street;
import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class TypeVariableWithDifferentNameExample {

    @SuppressWarnings("rawtypes")
    @Test
    public void typeVariableWithDifferentNameExample() {
        final GenericType<Street> genericType = GenericType.genericType(Street.class, Object.class);
        scenarioBuilderFor(genericType.toResolvedType())
                .withDeserializedForm(street("foo"))
                .withSerializedForm("\"foo\"")
                .withSerializationSuccessful()
                .withDeserializationFailing()
                .withDuplexFailing()
                .withManual((mapMaidBuilder, capabilities) -> mapMaidBuilder.withCustomType(capabilities,
                        customPrimitive(genericType, Street::stringValue, Street::street)))
                .run();
    }
}

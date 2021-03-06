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

package de.quantummaid.mapmaid.json;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.*;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class AutoloadingSpecs {

    @Test
    public void jacksonMarshallersAreAutoloadable() {
        given(aMapMaid()
                .serializingAndDeserializing(MySerializedObject.class)
                .build()
        )
                .when().mapMaidSerializes(new MySerializedObject("a", "b", "c")).withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("{\"field3\":\"c\",\"field2\":\"b\",\"field1\":\"a\"}");
    }

    @Test
    public void autoloadingFindsAllAvailableJacksonMarshallers() {
        given(mapMaidWillBeInstiantedUsing(() -> aMapMaid().build()))
                .when().theSupportedMarshallingTypesAreQueried()
                .mapMaidKnowsAboutMarshallingTypes(JSON, YAML, XML)
                .mapMaidKnowsAboutUnmarshallingTypes(JSON, YAML, XML);
    }

    private Supplier<MapMaid> mapMaidWillBeInstiantedUsing(final Supplier<MapMaid> supplier) {
        return supplier;
    }
}

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

package de.quantummaid.mapmaid.specs.examples.serializedobjects.conflicting.mixed_getter_fields;

import de.quantummaid.mapmaid.specs.examples.customprimitives.success.normal.example1.Name;
import de.quantummaid.mapmaid.specs.examples.customprimitives.success.normal.example2.TownName;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class MixedGetterFieldsExample {

    @Test
    public void mixedGetterFieldsExample() {
        final AddALotRequest addALotRequest = new AddALotRequest();
        addALotRequest.setName(Name.fromStringValue("qwer"));
        addALotRequest.setTownNameA(TownName.townName("a"));
        addALotRequest.setTownNameB(TownName.townName("b"));
        addALotRequest.setTownNameC(TownName.townName("c"));

        scenarioBuilderFor(AddALotRequest.class)
                .withDeserializedForm(addALotRequest)
                .withSerializedForm("" +
                        "{\n" +
                        "  \"name\": \"qwer\",\n" +
                        "  \"townNameA\": \"a\",\n" +
                        "  \"townNameB\": \"b\",\n" +
                        "  \"townNameC\": \"c\"\n" +
                        "}"
                )
                .withAllScenariosSuccessful()
                .run();
    }
}

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

package de.quantummaid.mapmaid.builder;

import com.google.gson.Gson;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.models.customconvention.Body;
import de.quantummaid.mapmaid.builder.models.customconvention.Email;
import de.quantummaid.mapmaid.builder.models.customconvention.EmailAddress;
import de.quantummaid.mapmaid.builder.models.customconvention.Subject;
import de.quantummaid.mapmaid.shared.validators.CustomTypeValidationException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class CustomConventionalBuilderTest {

    public static final String EMAIL_JSON = "{" +
            "\"receiver\":\"receiver@example.com\"," +
            "\"body\":\"Hello World!!!\"," +
            "\"sender\":\"sender@example.com\"," +
            "\"subject\":\"Hello\"" +
            "}";
    public static final Email EMAIL = Email.restore(
            EmailAddress.deserialize("sender@example.com"),
            EmailAddress.deserialize("receiver@example.com"),
            Subject.deserialize("Hello"),
            Body.deserialize("Hello World!!!")
    );

    public static MapMaid theCustomConventionalMapMaidInstance() {
        final Gson gson = new Gson();

        return MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
                .withAdvancedSettings(advancedBuilder -> {
                    advancedBuilder.withPreferredSerializedObjectFactoryName("restore");
                    advancedBuilder.withPreferredCustomPrimitiveFactoryName("deserialize");
                    advancedBuilder.withPreferredCustomPrimitiveSerializationMethodName("serialize");
                    advancedBuilder.usingJsonMarshaller(gson::toJson, input -> gson.fromJson(input, Object.class));
                })
                .withExceptionIndicatingValidationError(CustomTypeValidationException.class)
                .build();
    }

    @Test
    public void testEmailSerialization() {
        final String result = theCustomConventionalMapMaidInstance().serializeToJson(EMAIL);
        assertThat(result, is(EMAIL_JSON));
    }

    @Test
    public void testEmailDeserialization() {
        final Email result = theCustomConventionalMapMaidInstance().deserializeJson(EMAIL_JSON, Email.class);
        assertThat(result, is(EMAIL));
    }
}

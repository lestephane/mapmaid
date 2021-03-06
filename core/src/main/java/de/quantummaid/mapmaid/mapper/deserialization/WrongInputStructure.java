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

package de.quantummaid.mapmaid.mapper.deserialization;

import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;

public final class WrongInputStructure {

    private WrongInputStructure() {
    }

    public static MapMaidException wrongInputStructureException(final Class<? extends Universal> expected,
                                                                final Universal actual,
                                                                final String location,
                                                                final ScanInformation... scanInformations) {
        NotNullValidator.validateNotNull(expected, "expected");
        NotNullValidator.validateNotNull(actual, "actual");
        NotNullValidator.validateNotNull(location, "location");
        final String message = String.format(
                "Requiring the input to be an '%s' but found '%s' at '%s'",
                Universal.describe(expected),
                actual.toNativeJava(),
                location);
        return mapMaidException(message, scanInformations);
    }
}

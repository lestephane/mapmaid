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

package de.quantummaid.mapmaid.mapper.definitions;

import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;

public final class DefinitionNotFoundException extends RuntimeException {
    private DefinitionNotFoundException(final String msg) {
        super(msg);
    }

    public static DefinitionNotFoundException definitionNotFound(
            final TypeIdentifier targetType, final String dump) {
        final String msg = String.format(
                "no definition found for type '%s'. Known definitions are: %n%s",
                targetType.description(), dump);
        return new DefinitionNotFoundException(msg);
    }
}

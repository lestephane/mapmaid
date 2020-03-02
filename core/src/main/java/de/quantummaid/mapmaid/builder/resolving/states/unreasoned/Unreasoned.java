/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.resolving.states.unreasoned;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Reason;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.states.undetected.UndetectedDeserializer.undetectedDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.states.undetected.UndetectedSerializer.undetectedSerializer;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class Unreasoned extends StatefulDefinition {

    private Unreasoned(final Context context) {
        super(context);
    }

    public static StatefulDefinition unreasoned(final Context context) {
        return new Unreasoned(context);
    }

    @Override
    public StatefulDefinition addSerialization(final Reason reason) {
        this.context.scanInformationBuilder().addSerializationReason(reason);
        return undetectedSerializer(this.context);
    }

    @Override
    public StatefulDefinition removeSerialization(final Reason reason) {
        return this;
    }

    @Override
    public StatefulDefinition addDeserialization(final Reason reason) {
        this.context.scanInformationBuilder().addDeserializationReason(reason);
        return undetectedDeserializer(this.context);
    }

    @Override
    public StatefulDefinition removeDeserialization(final Reason reason) {
        return this;
    }
}
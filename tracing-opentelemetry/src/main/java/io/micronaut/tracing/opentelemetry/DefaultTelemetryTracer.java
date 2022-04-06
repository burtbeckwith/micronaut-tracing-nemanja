/*
 * Copyright 2017-2022 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.tracing.opentelemetry;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.tracing.DefaultTracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.opentracingshim.OpenTracingShim;
import io.opentracing.Tracer;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;

import static io.micronaut.runtime.ApplicationConfiguration.APPLICATION_NAME;

/**
 * Creates a default NoopTracer if no other tracer is present.
 *
 * @author Nemanja Mikic
 */
@Factory
@Requires(property = APPLICATION_NAME)
@Replaces(factory = DefaultTracer.class)
public class DefaultTelemetryTracer {

    @Property(name = APPLICATION_NAME)
    String applicationName;

    /**
     * Creates a default {@link io.opentelemetry.api.trace.Tracer} if no other <code>Tracer</code> is present.
     * @param openTelemetry the openTelemetry
     * @return no-op <code>Tracer</code>
     */

    @Singleton
    @Primary
    Tracer defaultTracer(@NotNull OpenTelemetry openTelemetry) {
        return OpenTracingShim.createTracerShim(openTelemetry.getTracer(applicationName));
    }

}

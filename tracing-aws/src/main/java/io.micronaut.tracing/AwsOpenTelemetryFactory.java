package io.micronaut.tracing;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.contrib.awsxray.AwsXrayIdGenerator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.aws.AwsXrayPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import jakarta.inject.Singleton;


@Factory
@Replaces(factory = DefaultOpenTelemetryFactory.class)
public class AwsOpenTelemetryFactory {

    @Singleton
    @Primary
    OpenTelemetry defaultOpenTelemetryWithConfig(@NonNull AwsOpenTelemetryConfiguration awsOpenTelemetryConfiguration) {
        String endpoint = "http://localhost:4317";

        if (!StringUtils.isEmpty(awsOpenTelemetryConfiguration.getOtlpGrpcEndpoint())) {
            endpoint = awsOpenTelemetryConfiguration.getOtlpGrpcEndpoint();
        }

        return OpenTelemetrySdk.builder()
            // This will enable your downstream requests to include the X-Ray trace header
            .setPropagators(
                ContextPropagators.create(
                    TextMapPropagator.composite(
                        W3CTraceContextPropagator.getInstance(), AwsXrayPropagator.getInstance())))

            // This provides basic configuration of a TracerProvider which generates X-Ray compliant IDs
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(
                        BatchSpanProcessor.builder(
                            OtlpGrpcSpanExporter.builder()
                                .setEndpoint(endpoint)
                                .build()
                        ).build())
                    .setIdGenerator(AwsXrayIdGenerator.getInstance())
                    .build())
            .buildAndRegisterGlobal();
    }

    @Singleton
    OpenTelemetry defaultOpenTelemetry() {
        return OpenTelemetrySdk.builder()
            // This will enable your downstream requests to include the X-Ray trace header
            .setPropagators(
                ContextPropagators.create(
                    TextMapPropagator.composite(
                        W3CTraceContextPropagator.getInstance(), AwsXrayPropagator.getInstance())))

            // This provides basic configuration of a TracerProvider which generates X-Ray compliant IDs
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(
                        BatchSpanProcessor.builder(
                            OtlpGrpcSpanExporter.builder()
                                .build()
                        ).build())
                    .setIdGenerator(AwsXrayIdGenerator.getInstance())
                    .build())
            .buildAndRegisterGlobal();
    }

}
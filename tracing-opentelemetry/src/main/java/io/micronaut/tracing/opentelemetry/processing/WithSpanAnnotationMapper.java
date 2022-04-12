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
package io.micronaut.tracing.opentelemetry.processing;

import io.micronaut.aop.InterceptorBinding;
import io.micronaut.aop.InterceptorKind;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.TypedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.opentelemetry.extension.annotations.WithSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds interceptor binding to {@link WithSpan} and adds {@link ContinueSpan} to it.
 *
 * @author Nemanja Mikic
 */
public class WithSpanAnnotationMapper implements TypedAnnotationMapper<WithSpan> {

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<WithSpan> annotation, VisitorContext visitorContext) {
        AnnotationValue<InterceptorBinding> interceptorBinding = AnnotationValue.builder(InterceptorBinding.class)
            .value(ContinueSpan.class)
            .member("kind", InterceptorKind.AROUND)
            .build();

        AnnotationValue<ContinueSpan> continueSpan = AnnotationValue.builder(ContinueSpan.class)
            .value(annotation.stringValue().orElse(null))
            .build();

        ArrayList<AnnotationValue<?>> test = new ArrayList<>();

        test.add(interceptorBinding);
        test.add(continueSpan);

        return test;
    }

    @Override
    public Class<WithSpan> annotationType() {
        return WithSpan.class;
    }
}


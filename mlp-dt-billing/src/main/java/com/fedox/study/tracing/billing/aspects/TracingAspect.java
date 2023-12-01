package com.fedox.study.tracing.billing.aspects;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Aspect
@Slf4j
public class TracingAspect {

    private final Tracer tracer;

    @Autowired
    public TracingAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Pointcut("execution(* com.fedox.study.tracing.billing.api.BillingController.payment(..))")
    public void paymentApiCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.billing.services.CRUDService.pay(..))")
    public void payServiceCall() {}

    @Around("paymentApiCall()")
    public Object traceApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        var spanContext = tracer.extract(
                Format.Builtin.HTTP_HEADERS,
                new HttpHeadersCarrier(httpHeaders)
        );

        if (Objects.nonNull(spanContext)) {
            return buildSpanFromContextAndProceed(spanContext, joinPoint);
        }

        log.debug("no context was injected on endpoint call");

        return buildSpanAndProceed(joinPoint);
    }


    @Around("payServiceCall()")
    public Object traceServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        return buildSpanAndProceed(joinPoint);
    }

    private Object buildSpanFromContextAndProceed(SpanContext injectedSpanContext, ProceedingJoinPoint joinPoint) throws Throwable {
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .asChildOf(injectedSpanContext)
                .start();
        span.setTag(TracingConstants.SPAN_APP_METHOD_TAG, getFQDN(joinPoint));
        try (var scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed();
        } finally {
            span.finish();
        }
    }

    private SpanContext extractSpanContext(ProceedingJoinPoint joinPoint) {
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        return tracer.extract(
                Format.Builtin.HTTP_HEADERS,
                new HttpHeadersCarrier(httpHeaders)
        );
    }

    private Object buildSpanAndProceed(ProceedingJoinPoint joinPoint) throws Throwable {
        var span = tracer.buildSpan(joinPoint.getSignature().getName()).start();
        span.setTag(TracingConstants.SPAN_APP_METHOD_TAG, getFQDN(joinPoint));
        try (var scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed();
        } finally {
            span.finish();
        }
    }

    private String getFQDN(ProceedingJoinPoint joinPoint) {
        return String.format("%s.%s",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

}

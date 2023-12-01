package com.fedox.study.tracing.logistic.aspects;

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

    @Pointcut("execution(* com.fedox.study.tracing.logistic.api.EShopLogistic.transport(..))")
    public void transportControllerCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.logistic.services.TransportService.transport(..))")
    public void transportCall() {}

    @Around("transportControllerCall()")
    public Object traceControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        SpanContext spanContext = tracer.extract(
                Format.Builtin.TEXT_MAP,
                new HttpHeadersCarrier(httpHeaders)
        );
        if (Objects.nonNull(spanContext)) log.debug("received trace: {}", spanContext.toTraceId());
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .asChildOf(spanContext)
                .start();
        span.setTag("app.method", getFQDN(joinPoint));
        try (var scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed();
        } finally {
            span.finish();
        }
    }

    @Around("transportCall()")
    public Object traceServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        var span = tracer.buildSpan(joinPoint.getSignature().getName()).start();
        span.setTag("app.method", getFQDN(joinPoint));
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

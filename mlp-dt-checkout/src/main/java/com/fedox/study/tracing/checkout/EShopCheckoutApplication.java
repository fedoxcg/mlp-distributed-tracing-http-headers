package com.fedox.study.tracing.checkout;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.tracerresolver.internal.JaegerTracerFactory;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableAspectJAutoProxy
@PropertySource("classpath:application.yaml")
public class EShopCheckoutApplication {

	@Value("${tracer.trace.name}")
	private String traceName;

	public static void main(String[] args) {
		SpringApplication.run(EShopCheckoutApplication.class, args);
	}

	@Bean
	public Tracer tracer() {
		return Configuration.fromEnv(traceName).getTracer();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}

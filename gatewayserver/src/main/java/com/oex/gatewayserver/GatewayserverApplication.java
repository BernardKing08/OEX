package com.oex.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator oexRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p.path("/oex/authentication/**")
						.filters(f -> f.rewritePath("/oex/authentication/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()) 
								.circuitBreaker(config -> config.setName("authenticationCircuitBreaker")
									.setFallbackUri("forward:/contactSupport"))
									.retry(config -> config.setRetries(3) 
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofSeconds(1), 2, true)))
						.uri("lb://AUTHENTICATION")) 
				.route(p -> p.path("/oex/examination/**")
						.filters(f -> f.rewritePath("/oex/examination/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("examinationCircuitBreaker")
									.setFallbackUri("forward:/contactSupport"))
									.retry(config -> config.setRetries(3) 
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofSeconds(1), 2, true)))
						.uri("lb://EXAMINATION"))
				.route(p -> p.path("/oex/notification/**")
						.filters(f -> f.rewritePath("/oex/notification/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("notificationCircuitBreaker")
									.setFallbackUri("forward:/contactSupport"))
									.retry(config -> config.setRetries(3) 
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofSeconds(1), 2, true)))
						.uri("lb://NOTIFICATION"))
				.route(p -> p.path("/oex/result/**")
						.filters(f -> f.rewritePath("/oex/result/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("resultCircuitBreaker")
									.setFallbackUri("forward:/contactSupport"))
									.retry(config -> config.setRetries(3) 
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofSeconds(1), 2, true)))
						.uri("lb://RESULT"))
				.build();
	}

}

package de.vsfexperts.latex.server.configuration;

import org.springframework.context.annotation.Configuration;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

@Configuration
@EnableMetrics
public class MetricsConfiguration extends MetricsConfigurerAdapter {

}

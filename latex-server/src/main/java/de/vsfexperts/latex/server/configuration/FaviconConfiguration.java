package de.vsfexperts.latex.server.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Configuration
public class FaviconConfiguration {

	byte[] favIcon = new byte[] { (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };

	@Bean
	public SimpleUrlHandlerMapping faviconMapping() {
		final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setOrder(Integer.MIN_VALUE);
		mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));

		return mapping;
	}

	@Bean
	protected ResourceHttpRequestHandler faviconRequestHandler() {

		final ClassPathResource classPathResource = new ClassPathResource("favicon.ico");
		final List<Resource> locations = Arrays.asList(classPathResource);

		final ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
		requestHandler.setLocations(locations);
		requestHandler.setCacheSeconds(Integer.MAX_VALUE);

		return requestHandler;
	}
}
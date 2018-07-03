package de.vsfexperts.latex.renderer;

import static de.vsfexperts.latex.renderer.ClasspathUtils.getContent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.springframework.http.HttpStatus.ACCEPTED;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ApiIT {

	private static final Logger LOG = LoggerFactory.getLogger(ApiIT.class);
	private static final String baseUrl = "http://localhost:8080";

	private RestTemplate rest;

	@Before
	public void setUp() {
		final HttpMessageConverter<String> messageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));

		rest = new RestTemplate();
		rest.getMessageConverters().add(0, messageConverter);
	}

	@Test
	public void testPdfRendering() throws IOException {
		final String latexDocument = getContent("/META-INF/sample.tex");

		final ResponseEntity<UUID> result = renderPdf(latexDocument);

		assertThat(result.getBody(), is(not(nullValue())));
		assertThat(result.getStatusCode(), is(ACCEPTED));
	}

	@Test
	public void testRenderingResult() throws Exception {
		final String latexDocument = getContent("/META-INF/sample.tex");

		final UUID jobId = renderPdf(latexDocument).getBody();
		assumeThat(jobId, is(not(nullValue())));

		final byte[] fetchResult = fetchResult(jobId);

		assertThat(fetchResult, is(not(nullValue())));
	}

	private byte[] fetchResult(final UUID jobId) throws InterruptedException {
		int maxRetries = 5;
		ResponseEntity<byte[]> result = null;

		while (maxRetries > 0) {
			try {
				result = fetchPdf(jobId);
			} catch (final HttpClientErrorException e) {
				LOG.warn("Attempt to retrieve pdf has failed. {} retries left", maxRetries - 1);
				Thread.sleep(1000l);
			} finally {
				maxRetries--;
			}
		}

		if (result == null) {
			return null;
		}

		return result.getBody();

	}

	private ResponseEntity<UUID> renderPdf(final String latexDocument) {
		return rest.postForEntity(baseUrl, latexDocument, UUID.class);
	}

	private ResponseEntity<byte[]> fetchPdf(final UUID jobId) {
		return rest.getForEntity(baseUrl + "/" + jobId, byte[].class);
	}

}

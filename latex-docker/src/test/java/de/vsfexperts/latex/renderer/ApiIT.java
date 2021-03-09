package de.vsfexperts.latex.renderer;

import static de.vsfexperts.latex.renderer.ClasspathUtils.getContent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
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
	private static final String TEST_DOCUMENT = "/META-INF/sample.tex";
	private static final String BASE_URL = "http://localhost:8080";
	private static final int MAX_RETRIES = 5;

	private RestTemplate rest;

	@Before
	public void setUp() {
		final HttpMessageConverter<String> messageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));

		rest = new RestTemplate();
		rest.getMessageConverters().add(0, messageConverter);
	}

	@Test
	public void testDocumentSubmission() throws IOException {
		final String latexDocument = getContent(TEST_DOCUMENT);

		final ResponseEntity<UUID> result = render(latexDocument);

		assertThat(result.getBody(), is(not(nullValue())));
		assertThat(result.getStatusCode(), is(ACCEPTED));
	}

	@Test
	public void testRenderingResult() throws Exception {
		final String latexDocument = getContent(TEST_DOCUMENT);

		final UUID jobId = render(latexDocument).getBody();
		assumeThat(jobId, is(not(nullValue())));

		final byte[] pdf = downloadPdf(jobId);
		assertThat(pdf, is(not(nullValue())));
	}

	private byte[] downloadPdf(final UUID jobId) throws InterruptedException {
		ResponseEntity<byte[]> result = null;

		int retries = MAX_RETRIES;
		while (retries > 0) {
			try {
				result = fetchOutput(jobId);
			} catch (final HttpClientErrorException e) {
				LOG.warn("Attempt to retrieve pdf has failed. {} retries left", retries - 1);
				Thread.sleep(1000l);
			} finally {
				retries--;
			}
		}

		if (result == null || result.getStatusCode().isError()) {
			return null;
		}

		return result.getBody();

	}

	private ResponseEntity<UUID> render(final String latexDocument) {
		return rest.postForEntity(BASE_URL, latexDocument, UUID.class);
	}

	private ResponseEntity<byte[]> fetchOutput(final UUID jobId) {
		return rest.getForEntity(BASE_URL + "/" + jobId, byte[].class);
	}

}

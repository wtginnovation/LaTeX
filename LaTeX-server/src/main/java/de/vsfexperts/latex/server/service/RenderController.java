package de.vsfexperts.latex.server.service;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import de.vsfexperts.latex.renderer.LatexRenderer;

@RestController
public class RenderController {

	@Autowired
	private LatexRenderer renderer;

	@PostMapping(value = "/", produces = APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(ACCEPTED)
	@Timed
	public UUID renderJob(@RequestBody @Valid @NotNull final String template) {
		return renderer.render(template);
	}

	@GetMapping(value = "/{jobId}", produces = APPLICATION_PDF_VALUE)
	@Timed
	public FileSystemResource renderResult(@PathVariable("jobId") @Valid @NotNull final UUID jobId)
			throws FileNotFoundException {

		final Optional<File> pdf = renderer.getResult(jobId);

		return new FileSystemResource(
				pdf.orElseThrow(() -> new FileNotFoundException("Getting output of " + jobId + " failed")));
	}

	@ExceptionHandler(FileNotFoundException.class)
	@ResponseStatus(NOT_FOUND)
	public void handleFileNotFoundError() {
	}

}

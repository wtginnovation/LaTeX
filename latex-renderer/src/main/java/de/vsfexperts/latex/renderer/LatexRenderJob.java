package de.vsfexperts.latex.renderer;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vsfexperts.latex.storage.Archive;
import de.vsfexperts.latex.storage.LocalFileSystemStorage;

/**
 * Renders a LaTeX file. Only single pass rendering is supported atm.
 */
class LatexRenderJob implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(LatexRenderJob.class);

	private final UUID id = UUID.randomUUID();
	private final String template;

	private final LocalFileSystemStorage storage;
	private final Archive archive;

	public LatexRenderJob(final LocalFileSystemStorage storage, final Archive archive, final String template) {
		this.template = template;
		this.storage = storage;
		this.archive = archive;
	}

	@Override
	public void run() {
		try {
			runInternal();
		} catch (final LatexRenderingException e) {
			throw e;
		} catch (final Exception e) {
			throw new LatexRenderingException(e);
		} finally {
			cleanupRenderOutput();
		}
	}

	private void runInternal() throws IOException, InterruptedException {
		final File texDirectory = storage.getBaseDirectory(id);
		final File texFile = storage.createFile(id, id + ".tex", template);

		renderPdf(texDirectory.getAbsolutePath(), texFile);
		renderPdf(texDirectory.getAbsolutePath(), texFile);

		archiveSourceDocument(texFile);
		archivePdf();
	}

	private void renderPdf(final String texDirectoryPath, final File texFile) throws IOException, InterruptedException {
		final Process pdfRenderer = buildRenderProcess(texDirectoryPath, texFile).start();
		new Thread(new ProcessOutputLogger(pdfRenderer)).start();
		pdfRenderer.waitFor();
	}

	private ProcessBuilder buildRenderProcess(final String texDirectoryPath, final File texFile) {
		final String executable = "pdflatex";
		final String batchModeSwitch = "-interaction=nonstopmode";
		final String stopOnErrorSwitch = "-halt-on-error";
		final String outputDirectory = "-output-directory=" + texDirectoryPath;
		final String auxDirectory = "-aux-directory=" + texDirectoryPath;

		final ProcessBuilder processBuilder = new ProcessBuilder(executable, batchModeSwitch, stopOnErrorSwitch,
				outputDirectory, auxDirectory, texFile.getAbsolutePath());
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(new File(texDirectoryPath));

		return processBuilder;
	}

	private void cleanupRenderOutput() {
		storage.delete(id);
	}

	private void archiveSourceDocument(final File texFile) {
		archive.store(id, texFile);
	}

	private void archivePdf() {
		final Optional<File> pdfFile = storage.getFile(id, id + ".pdf");
		if (pdfFile.isPresent()) {
			archive.store(id, pdfFile.get());
		}
	}

	public UUID getId() {
		return id;
	}

	private static class ProcessOutputLogger implements Runnable {
		private final Process process;

		ProcessOutputLogger(final Process process) {
			this.process = process;
		}

		@Override
		public void run() {
			try (Scanner processOutput = new Scanner(new InputStreamReader(process.getInputStream()))) {
				while (processOutput.hasNextLine()) {
					LOG.trace(processOutput.nextLine());
				}
			}
		}
	}

}

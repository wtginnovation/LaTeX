package de.vsfexperts.latex.renderer;

import static de.vsfexperts.latex.renderer.ClasspathUtils.getContent;
import static de.vsfexperts.latex.renderer.FileMatcher.notAnEmptyFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.vsfexperts.latex.storage.FileSystemArchive;
import de.vsfexperts.latex.storage.LocalFileSystemStorage;

public class SynchronousLatexRendererIT {

	private LocalFileSystemStorage tempStorage;
	private LatexRenderer renderer;
	private FileSystemArchive archive;

	@Before
	public void setUp() {
		tempStorage = new LocalFileSystemStorage();
		archive = new FileSystemArchive();
		renderer = new SynchronousLatexRenderer(tempStorage, archive);
	}

	@After
	public void tearDown() {
		archive.cleanArchive();
	}

	@Test
	public void testPdfRendering() throws Exception {
		final String latexDocument = getContent("/META-INF/sample.tex");

		final UUID jobId = renderer.render(latexDocument);

		try {

			final File pdfRenderOutput = renderer.getResult(jobId).get();
			assertThat(pdfRenderOutput, is(notAnEmptyFile()));

			final File archivedPdfOutput = archive.get(jobId, jobId.toString() + ".pdf").get();
			assertThat(archivedPdfOutput, is(notAnEmptyFile()));

			final File archivedTexDocument = archive.get(jobId, jobId.toString() + ".tex").get();
			assertThat(archivedTexDocument, is(notAnEmptyFile()));

		} finally {
			tempStorage.delete(jobId);
		}
	}

	@Test
	public void testInvalidPdfRendering() throws Exception {
		final String latexDocument = "INVALID TEX DOCUMENT";

		final UUID jobId = renderer.render(latexDocument);

		try {

			final File baseDirectory = tempStorage.getBaseDirectory(jobId);
			assertThat(baseDirectory.list().length, is(0));

		} finally {
			tempStorage.delete(jobId);
		}
	}

}

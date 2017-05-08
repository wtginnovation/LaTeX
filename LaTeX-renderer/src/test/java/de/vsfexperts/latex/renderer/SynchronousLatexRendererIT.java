package de.vsfexperts.latex.renderer;

import static de.vsfexperts.latex.renderer.ClasspathUtils.getContent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.vsfexperts.latex.storage.FileSystemArchive;
import de.vsfexperts.latex.storage.LocalFileSystemStorage;

public class SynchronousLatexRendererIT {

	private LocalFileSystemStorage workDirectory;
	private LatexRenderer renderer;
	private FileSystemArchive archive;

	@Before
	public void setUp() {
		workDirectory = new LocalFileSystemStorage();
		archive = new FileSystemArchive();
		renderer = new SynchronousLatexRenderer(workDirectory, archive);
	}
	
	@After
	public void tearDown() {
		archive.cleanArchive();
	}

	@Test
	public void testPdfRendering() throws Exception {
		final String template = getContent("/META-INF/sample.tex");

		final UUID jobId = renderer.render(template);
		try {
			final File pdfFileLocation = renderer.getResult(jobId).get();

			assertThat(pdfFileLocation.exists(), is(true));
			assertThat(pdfFileLocation.isFile(), is(true));
			assertThat(FileUtils.sizeOf(pdfFileLocation), is(not(0)));
		} finally {
			new LocalFileSystemStorage().delete(jobId);
		}
	}

	@Test
	public void testInvalidPdfRendering() throws Exception {
		final String template = "NOT A TEX FILE";

		final UUID jobId = renderer.render(template);
		final File baseDirectory = workDirectory.getBaseDirectory(jobId);
		assertThat(baseDirectory.list().length, is(0));

		new LocalFileSystemStorage().delete(jobId);
	}

}

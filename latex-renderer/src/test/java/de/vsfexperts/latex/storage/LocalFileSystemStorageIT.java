package de.vsfexperts.latex.storage;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LocalFileSystemStorageIT {

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private LocalFileSystemStorage storage;
	private UUID jobId;

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		storage = new LocalFileSystemStorage();
		jobId = UUID.randomUUID();
	}

	@After
	public void tearDown() {
		storage.delete(jobId);
	}

	@Test
	public void testCreationOfBaseDirectory() {
		final File directory = storage.getBaseDirectory(jobId);

		assertThat(directory.exists(), is(true));
		assertThat(directory.getAbsolutePath(), containsString(FileUtils.getTempDirectoryPath()));
	}

	@Test
	public void testRecreationOfBaseDirectory() {
		final File oldDirectory = storage.getBaseDirectory(jobId);
		oldDirectory.delete();
		assumeThat(oldDirectory.exists(), is(false));

		final File directory = storage.getBaseDirectory(jobId);

		assertThat(directory.exists(), is(true));
		assertThat(directory.getAbsolutePath(), containsString(FileUtils.getTempDirectoryPath()));
	}

	@Test
	public void testFileCreation() throws Exception {
		final File file = storage.createFile(jobId, "filename.txt", "The content");

		assertThat(file.exists(), is(true));
		assertThat(FileUtils.readFileToString(file, UTF8), is("The content"));
		assertThat(file.getAbsolutePath(), containsString(storage.getBaseDirectory(jobId).getAbsolutePath()));
	}

	@Test
	public void testList() throws Exception {
		assumeThat(storage.list(jobId).isEmpty(), is(true));

		storage.createFile(jobId, "filename", "The content");

		final List<String> result = storage.list(jobId);
		assertThat(result.size(), is(1));
		assertThat(result, hasItem("filename"));
	}

	@Test
	public void testAddFile() throws Exception {
		final File file = testFolder.newFile();
		FileUtils.write(file, "The content", UTF8);
		storage.addFile(jobId, file);

		final File result = storage.getFile(jobId, file.getName()).get();

		assertThat(result.exists(), is(true));
		assertThat(FileUtils.readFileToString(result, UTF8), is("The content"));
		assertThat(result.getAbsolutePath(), containsString(storage.getBaseDirectory(jobId).getAbsolutePath()));
	}

	@Test(expected = FileSystemException.class)
	public void testAddNonExistantFile() throws Exception {
		storage.addFile(jobId, new File(testFolder.getRoot(), "nonExistant"));
		fail();
	}

	@Test
	public void testRetrievalOfNonExistingFiles() throws Exception {
		final Optional<File> file = storage.getFile(jobId, "nonExistant");

		assertThat(file.isPresent(), is(false));
	}

	@Test
	public void testRetrievalOfExistingFile() throws Exception {
		storage.createFile(jobId, "filename.txt", "The content");

		final File file = storage.getFile(jobId, "filename.txt").get();

		assertThat(file.exists(), is(true));
	}

	@Test
	public void testDeletion() throws Exception {
		assumeThat(storage.list(jobId).isEmpty(), is(true));

		storage.createFile(jobId, "filename", "The content");
		assertThat(storage.list(jobId).isEmpty(), is(false));

		storage.delete(jobId, "filename");
		assertThat(storage.list(jobId).isEmpty(), is(true));
	}

	@Test(expected = FileSystemException.class)
	public void testDeletionNonexistantFile() throws Exception {
		assumeThat(storage.list(jobId).isEmpty(), is(true));

		storage.delete(jobId, "nonExistant");
		fail();
	}

	@Test
	public void testDeletionWithFilter() throws Exception {
		assumeThat(storage.list(jobId).isEmpty(), is(true));

		storage.createFile(jobId, "filename.pdf", "The content");
		storage.createFile(jobId, "filename.txt", "The content");
		assumeThat(storage.list(jobId).size(), is(2));

		storage.deleteFiles(jobId, n -> n.endsWith(".pdf"));

		assertThat(storage.list(jobId).size(), is(1));
		assertThat(storage.list(jobId), hasItem("filename.txt"));
	}

	@Test
	public void testClear() {
		final File repoBaseDir = new File(testFolder.getRoot(), "repo");

		final LocalFileSystemStorage storage = new LocalFileSystemStorage(repoBaseDir);
		storage.createFile(jobId, "filename", null);

		assumeThat(storage.list(jobId).size(), is(1));

		storage.clear();

		assertThat(repoBaseDir.exists(), is(true));
		assertThat(repoBaseDir.list().length, is(0));
	}
}

package de.vsfexperts.latex.storage;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemArchiveIT {

	private FileSystemArchive archive;
	private UUID id;

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		archive = new FileSystemArchive();
		id = UUID.randomUUID();
	}

	@After
	public void tearDown() {
		archive.cleanArchive();
	}

	@Test
	public void testListEmptyArchive() {
		final List<String> contents = archive.list(id);
		assertThat(contents.isEmpty(), is(true));
	}

	@Test
	public void testStorageAndDeletion() throws IOException {
		final File file = File.createTempFile("pre", "", testFolder.getRoot());
		final String filename = file.getName();

		archive.store(id, file);

		final List<String> contentsAfterStore = archive.list(id);
		assertThat(contentsAfterStore, hasItem(filename));
		assertThat(contentsAfterStore.size(), is(1));

		assertThat(archive.get(id, filename).get().exists(), is(true));

		archive.delete(id, filename);

		final List<String> contentsAfterDelete = archive.list(id);
		assertThat(contentsAfterDelete.isEmpty(), is(true));
	}

	@Test(expected = ArchiveException.class)
	public void testStorageOfNonExistingFile() {
		archive.store(id, new File(testFolder.getRoot(), "nonExistant"));
		fail();
	}

	@Test
	public void testGetNonExistingFile() {
		final Optional<File> file = archive.get(id, "nonExistant");
		assertThat(file, is(Optional.empty()));
	}

	@Test(expected = ArchiveException.class)
	public void testDeleteNonExistingFile() {
		archive.delete(id, "nonExistant");
		fail();
	}

	@Test
	public void testCleanNonExistingArchive() {
		final String archiveBaseDir = testFolder.getRoot() + File.separator + "nonexistant";
		new FileSystemArchive(new LocalFileSystemStorage(archiveBaseDir)).cleanArchive();
	}

}

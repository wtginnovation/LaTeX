package de.vsfexperts.latex.storage;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

/**
 * Simple wrapper to ease file handling/centralize the storage of job related data. The default storage location is the
 * temp directory of your system. Instantiate with a different path to change this behaviour.
 */
public class LocalFileSystemStorage {

	private final String baseDirectory;

	public LocalFileSystemStorage() {
		this(FileUtils.getTempDirectoryPath());
	}

	public LocalFileSystemStorage(final File baseDirectory) {
		this(baseDirectory.getAbsolutePath());
	}

	public LocalFileSystemStorage(final String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public File getBaseDirectory(final UUID jobId) throws FileSystemException {
		requireNonNull(jobId);

		final File jobDirectory = new File(getGlobalBaseDirectory(), jobId.toString());

		try {
			FileUtils.forceMkdir(jobDirectory);
		} catch (final IOException e) {
			throw new FileSystemException(e);
		}

		return jobDirectory;
	}

	private File getGlobalBaseDirectory() throws FileSystemException {
		final File globalBaseDirectory = new File(baseDirectory);
		try {
			FileUtils.forceMkdir(globalBaseDirectory);
		} catch (final IOException e) {
			throw new FileSystemException(e);
		}
		return globalBaseDirectory;
	}

	public File createFile(final UUID jobId, final String filename, final String content) throws FileSystemException {
		requireNonNull(jobId);
		requireNonNull(filename);

		final File finalFile = new File(getBaseDirectory(jobId), filename);
		final File tempFile = new File(getBaseDirectory(jobId), filename + ".tmp");

		try {
			FileUtils.write(tempFile, content, Charset.forName("UTF-8"));
			FileUtils.moveFile(tempFile, finalFile);
		} catch (final IOException e) {
			throw new FileSystemException(e);
		}

		return finalFile;
	}

	public void addFile(final UUID jobId, final File file) throws FileSystemException {
		requireNonNull(jobId);
		requireNonNull(file);

		final File finalFile = new File(getBaseDirectory(jobId), file.getName());
		final File tempFile = new File(getBaseDirectory(jobId), file.getName() + ".tmp");

		try {
			FileUtils.copyFile(file, tempFile, true);
			FileUtils.moveFile(tempFile, finalFile);
		} catch (final IOException e) {
			throw new FileSystemException(e);
		}
	}

	public Optional<File> getFile(final UUID jobId, final String filename) throws FileSystemException {
		requireNonNull(jobId);
		requireNonNull(filename);

		final File file = new File(getBaseDirectory(jobId), filename);

		if (!file.exists()) {
			return Optional.empty();
		}

		return Optional.of(file);
	}

	public List<String> list(final UUID jobId) {
		requireNonNull(jobId);
		return Arrays.stream(getBaseDirectory(jobId).listFiles()).map(File::getName).collect(Collectors.toList());
	}

	public void delete(final UUID jobId, final String filename) throws FileSystemException {
		requireNonNull(jobId);
		requireNonNull(filename);

		try {
			FileUtils.forceDelete(new File(getBaseDirectory(jobId), filename));
		} catch (final IOException e) {
			throw new FileSystemException(e);
		}
	}

	public void delete(final UUID jobId) throws FileSystemException {
		requireNonNull(jobId);

		try {
			FileUtils.forceDelete(getBaseDirectory(jobId));
		} catch (final Exception e) {
			throw new FileSystemException(e);
		}
	}

	public void deleteFiles(final UUID jobId, final Predicate<String> filenamePredicate) {
		final FileFilter filter = f -> f.isFile() && filenamePredicate.test(f.getName());
		Arrays.stream(getBaseDirectory(jobId).listFiles(filter)).forEach(File::delete);
	}

	public void clear() throws FileSystemException {
		try {
			FileUtils.cleanDirectory(getGlobalBaseDirectory());
		} catch (final Exception e) {
			throw new FileSystemException(e);
		}
	}

}

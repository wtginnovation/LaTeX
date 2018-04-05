package de.vsfexperts.latex.storage;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

/**
 * Implementation of {@link Archive} to be used to store data within the local filesystem. The default archive folder is
 * located in the temp folder and named archive. Instantiate with your own {@link LocalFileSystemStorage} to change the
 * location.
 */
public class FileSystemArchive implements Archive {

	private final LocalFileSystemStorage storage;

	public FileSystemArchive() throws ArchiveException {
		this(new LocalFileSystemStorage(new File(FileUtils.getTempDirectory(), "archive")));
	}

	public FileSystemArchive(final LocalFileSystemStorage storage) {
		this.storage = storage;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.vsfexperts.latex.storage.Archive#store(java.util.UUID, java.io.File)
	 */
	@Override
	public void store(final UUID id, final File file) throws ArchiveException {
		requireNonNull(id);
		requireNonNull(file);
		try {
			storage.addFile(id, file);
		} catch (final Exception e) {
			throw new ArchiveException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.vsfexperts.latex.storage.Archive#delete(java.util.UUID, java.lang.String)
	 */
	@Override
	public void delete(final UUID id, final String name) throws ArchiveException {
		requireNonNull(id);
		requireNonNull(name);

		try {
			storage.delete(id, name);
		} catch (final Exception e) {
			throw new ArchiveException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.vsfexperts.latex.storage.Archive#list(java.util.UUID)
	 */
	@Override
	public List<String> list(final UUID id) {
		requireNonNull(id);

		try {
			return storage.list(id);
		} catch (final Exception e) {
			throw new ArchiveException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.vsfexperts.latex.storage.Archive#get(java.util.UUID, java.lang.String)
	 */
	@Override
	public Optional<File> get(final UUID id, final String name) {
		requireNonNull(id);
		requireNonNull(name);

		try {
			return storage.getFile(id, name);
		} catch (final Exception e) {
			throw new ArchiveException(e);
		}
	}

	public void cleanArchive() throws ArchiveException {
		try {
			storage.clear();
		} catch (final Exception e) {
			throw new ArchiveException(e);
		}
	}

}

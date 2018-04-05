package de.vsfexperts.latex.storage;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Archive {

	/**
	 * Stores the contents of the file in the archive. The filename is used by the archive to identify the object.
	 *
	 * @param id
	 *            to identify archive
	 * @param file
	 *            to be stored
	 */
	void store(UUID id, File file) throws ArchiveException;

	/**
	 * Deletes an archived object
	 *
	 * @param id
	 *            of archive
	 * @param name
	 *            of object to delete
	 */
	void delete(UUID id, String name) throws ArchiveException;

	/**
	 * List content of archive
	 *
	 * @param id
	 * @return List of objects
	 */
	List<String> list(UUID id);

	/**
	 * Returns an archived object
	 *
	 * @param id
	 *            of archive
	 * @param name
	 *            of object
	 * @return file pointing to storage location
	 */
	Optional<File> get(UUID id, String name);

}
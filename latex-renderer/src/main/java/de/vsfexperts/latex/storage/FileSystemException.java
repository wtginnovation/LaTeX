package de.vsfexperts.latex.storage;

import org.springframework.core.NestedRuntimeException;

/**
 * Something related to file persistence went wrong
 */
public class FileSystemException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public FileSystemException(final Throwable cause) {
		super("", cause);
	}

}

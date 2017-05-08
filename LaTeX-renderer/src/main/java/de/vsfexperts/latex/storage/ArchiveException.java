package de.vsfexperts.latex.storage;

import org.springframework.core.NestedRuntimeException;

public class ArchiveException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public ArchiveException(final Throwable cause) {
		super("", cause);
	}

}

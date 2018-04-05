package de.vsfexperts.latex.renderer;

import org.springframework.core.NestedRuntimeException;

public class LatexRenderingException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public LatexRenderingException(final Throwable cause) {
		super("Error rendering tex file ", cause);
	}

}

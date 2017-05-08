package de.vsfexperts.latex.template;

import org.springframework.core.NestedRuntimeException;

public class LatexTemplateProcessingException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public LatexTemplateProcessingException(final Throwable cause) {
		super("Error processing latex template", cause);
	}

	public LatexTemplateProcessingException(final String template) {
		super("Error processing latex template: " + template);
	}
}

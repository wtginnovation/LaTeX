package de.vsfexperts.latex.template;

import static java.util.Objects.requireNonNull;
import static org.apache.velocity.runtime.RuntimeConstants.INPUT_ENCODING;
import static org.apache.velocity.runtime.RuntimeConstants.OUTPUT_ENCODING;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADER;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.MathTool;

import de.vsfexperts.latex.template.tools.LatexEscapeTool;

/**
 * Template resolver for LaTeX files. Uses {@link LatexContext} to resolve variables.
 */
public class LatexTemplate {

	private final String template;

	public LatexTemplate(final String template) {
		this.template = requireNonNull(template);
	}

	public final String populateFrom(final LatexContext context) {
		requireNonNull(context);

		final VelocityEngine velocityEngine = initializeEngine();
		final VelocityContext velocityContext = prepareContext(context);

		return evaluateTemplate(velocityContext, velocityEngine);
	}

	private final VelocityEngine initializeEngine() {
		final VelocityEngine engine = new VelocityEngine();
		engine.setProperty(INPUT_ENCODING, "UTF-8");
		engine.setProperty(OUTPUT_ENCODING, "UTF-8");
		engine.setProperty(RUNTIME_LOG_LOGSYSTEM, new CommonsLogLogChute());

		customize(engine);
		engine.init();

		return engine;
	}

	protected void customize(final VelocityEngine engine) {
		engine.setProperty(RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	}

	private final VelocityContext prepareContext(final LatexContext context) {
		final VelocityContext velocityContext = new VelocityContext(context.asMap());
		customize(velocityContext);

		return velocityContext;
	}

	protected void customize(final VelocityContext velocityContext) {
		velocityContext.put("e", new LatexEscapeTool());
		velocityContext.put("math", new MathTool());
	}

	private final String evaluateTemplate(final VelocityContext velocityContext, final VelocityEngine engine) {
		final StringWriter writer = new StringWriter();

		try {
			final boolean successful = engine.evaluate(velocityContext, writer, this.getClass().getSimpleName(),
					template);

			if (!successful) {
				throw new LatexTemplateProcessingException(template);
			}

		} catch (final Exception e) {
			throw new LatexTemplateProcessingException(e);
		}

		return writer.getBuffer().toString();
	}

}

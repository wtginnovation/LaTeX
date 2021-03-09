package de.vsfexperts.latex.template;

import static de.vsfexperts.latex.template.ClasspathUtils.getContent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class LatexTemplateIT {

	@Test
	public void processTemplate() throws Exception {
		final String template = getContent("/META-INF/templates/sampleTemplate.tex");

		final LatexTemplate latexTemplate = new LatexTemplate(template);

		final LatexContext context = new LatexContext();
		context.add("CustomerName", "John Smith");

		final String result = latexTemplate.populateFrom(context);

		assertThat(result.contains("$"), is(false));
	}

}

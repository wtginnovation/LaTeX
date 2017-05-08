package de.vsfexperts.latex.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class LatexTemplateTest {

	@Test
	public void processTemplate() {
		LatexTemplate template = new LatexTemplate("${keyToReplace}");
		LatexContext context = new LatexContext();
		context.add("keyToReplace", "value");

		String result = template.populateFrom(context);

		assertThat(result, is("value"));
	}

	@Test(expected = NullPointerException.class)
	public void processNullTemplate() {
		new LatexTemplate(null);
		fail();
	}

	@Test(expected = NullPointerException.class)
	public void processNullContext() {
		new LatexTemplate("").populateFrom(null);
		fail();
	}

	@Test(expected = LatexTemplateProcessingException.class)
	public void processIncompleteTemplate() {
		LatexTemplate template = new LatexTemplate("${incompleteTemplate");
		template.populateFrom(new LatexContext());
		fail();
	}

}

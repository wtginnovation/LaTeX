package de.vsfexperts.latex.template;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class LatexContextTest {

	@Test
	public void testStandardOperation() {
		final LatexContext context = new LatexContext().add("null", null).add("default", "defaultValue");

		assertThat(context.keys().size(), is(2));
		assertThat(context.keys().contains("null"), is(true));
		assertThat(context.keys().contains("default"), is(true));

		assertThat(context.asMap().get("null"), is(nullValue()));
		assertThat(context.asMap().get("default"), is("defaultValue"));
	}

	@Test(expected = NullPointerException.class)
	public void testNullKey() {
		new LatexContext().add(null, null);
		fail();
	}

}

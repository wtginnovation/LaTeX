package de.vsfexperts.latex.template.tools;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class LatexEscapeToolTest {

	private LatexEscapeTool tool;

	@Before
	public void setUp() {
		tool = new LatexEscapeTool();
	}

	@Test
	public void testEscapingOfNull() {
		assertThat(tool.escape(null), is(nullValue()));
	}

	@Test
	public void testEscaping() {
		assertThat(tool.escape("\\"), is("\\textbackslash "));
		assertThat(tool.escape("<"), is("\\textless "));
		assertThat(tool.escape(">"), is("\\textgreater "));
		assertThat(tool.escape("§"), is("\\S "));
		assertThat(tool.escape("&"), is("\\&"));
		assertThat(tool.escape("%"), is("\\%"));
		assertThat(tool.escape("$"), is("\\$"));
		assertThat(tool.escape("#"), is("\\#"));
		assertThat(tool.escape("_"), is("\\_"));
		assertThat(tool.escape("{"), is("\\{"));
		assertThat(tool.escape("}"), is("\\}"));
		assertThat(tool.escape("~"), is("\\textasciitilde "));
		assertThat(tool.escape("^"), is("\\textasciicircum "));
	}

	@Test
	public void testMultipleCharacters() {
		final String expected = "!\"\\S '\\$\\%\\&/()=¿?[]\\{\\}\\euro @\\textasciicircum \\textasciitilde \\textbackslash \\#ßôâéè\\textless \\textgreater ";
		assertThat(tool.escape("!\"§'$%&/()=¿?[]{}€@^~\\#ßôâéè<>"), is(expected));
	}

}

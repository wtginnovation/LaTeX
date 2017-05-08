package de.vsfexperts.latex.template.tools;

/**
 * Velocity tool to escape LaTeX (invalid) special characters
 */
public class LatexEscapeTool {

	public String escape(final String input) {
		if (input == null) {
			return null;
		}

		String output = input;

		// replace 1st, otherwise the substitution of \ will be applied twice (to other substitutions)
		output = output.replace("\\", "\\textbackslash ");

		output = output.replace("€", "\\euro ");
		output = output.replace("<", "\\textless ");
		output = output.replace(">", "\\textgreater ");
		output = output.replace("§", "\\S ");

		// LaTeX special characters
		output = output.replace("&", "\\&");
		output = output.replace("%", "\\%");
		output = output.replace("$", "\\$");
		output = output.replace("#", "\\#");
		output = output.replace("_", "\\_");
		output = output.replace("{", "\\{");
		output = output.replace("}", "\\}");
		output = output.replace("~", "\\textasciitilde ");
		output = output.replace("^", "\\textasciicircum ");

		return output;
	}
}

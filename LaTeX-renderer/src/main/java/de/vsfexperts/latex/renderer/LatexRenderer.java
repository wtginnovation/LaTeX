package de.vsfexperts.latex.renderer;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

/**
 * Renders a LaTeX document. The process might be synchronous or asynchronous, depending on the implementation of the
 * renderer. In general a render job is submitted via render. The availability of the job output(=pdf) can be queried by
 * calling the getResult method.
 */
public interface LatexRenderer {

	/**
	 * Trigger a latex render job
	 *
	 * @param template
	 *            to process
	 * @return id of render job
	 */
	UUID render(String template);

	/**
	 * Retrieve render output.
	 *
	 * @param jobId
	 *            to identify job
	 * @return file referencing the generated pdf
	 */
	Optional<File> getResult(UUID jobId);

}
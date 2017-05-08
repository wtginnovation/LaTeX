package de.vsfexperts.latex.renderer;

import java.util.concurrent.Executor;

import de.vsfexperts.latex.storage.Archive;
import de.vsfexperts.latex.storage.LocalFileSystemStorage;

/**
 * Single threaded renderer. This renderer is using the current thread to render the file in a synchronous way.
 */
public class SynchronousLatexRenderer extends GenericLatexRenderer {

	private static final DirectExecutor DIRECT_EXECUTOR = new DirectExecutor();

	public SynchronousLatexRenderer() {
	}

	public SynchronousLatexRenderer(final LocalFileSystemStorage storage, final Archive archive) {
		super(storage, archive);
	}

	@Override
	protected Executor getExecutor() {
		return DIRECT_EXECUTOR;
	}

	private static class DirectExecutor implements Executor {

		@Override
		public void execute(final Runnable r) {
			r.run();
		}

	}

}

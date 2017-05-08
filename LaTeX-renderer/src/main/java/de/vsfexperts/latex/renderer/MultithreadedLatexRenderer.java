package de.vsfexperts.latex.renderer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.vsfexperts.latex.storage.Archive;
import de.vsfexperts.latex.storage.LocalFileSystemStorage;

/**
 * Multi threaded renderer. The default size of the threadpool is determined by the number of available processors.
 */
public class MultithreadedLatexRenderer extends GenericLatexRenderer {

	private final Executor executor;

	public MultithreadedLatexRenderer() {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	public MultithreadedLatexRenderer(final LocalFileSystemStorage storage, final Archive archive,
			final Executor executor) {

		super(storage, archive);
		this.executor = executor;
	}

	@Override
	protected Executor getExecutor() {
		return executor;
	}

}

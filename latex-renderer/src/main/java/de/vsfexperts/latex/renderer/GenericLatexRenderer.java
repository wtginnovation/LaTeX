package de.vsfexperts.latex.renderer;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

import de.vsfexperts.latex.storage.Archive;
import de.vsfexperts.latex.storage.FileSystemArchive;
import de.vsfexperts.latex.storage.LocalFileSystemStorage;

/**
 * Submits render jobs to an executor. Subclasses must provide an implementation of the execution strategy. See
 * {@link MultithreadedLatexRenderer} and {@link SynchronousLatexRenderer} for default implementations.
 */
public abstract class GenericLatexRenderer implements LatexRenderer {

	private final LocalFileSystemStorage storage;
	private final Archive archive;

	public GenericLatexRenderer() {
		this(new LocalFileSystemStorage(), new FileSystemArchive());
	}

	public GenericLatexRenderer(final LocalFileSystemStorage storage, final Archive archive) {
		this.storage = storage;
		this.archive = archive;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.vsfexperts.latex.renderer.LatexRenderer#render(java.lang.String)
	 */
	@Override
	public final UUID render(final String template) {
		requireNonNull(template);

		final LatexRenderJob job = new LatexRenderJob(storage, archive, template);

		final UUID jobId = job.getId();

		getExecutor().execute(job);

		return jobId;
	}

	protected abstract Executor getExecutor();

	/*
	 * (non-Javadoc)
	 *
	 * @see de.vsfexperts.latex.renderer.LatexRenderer#getResult(java.util.UUID)
	 */
	@Override
	public final Optional<File> getResult(final UUID jobId) {
		requireNonNull(jobId);

		return archive.get(jobId, jobId + ".pdf");
	}

}

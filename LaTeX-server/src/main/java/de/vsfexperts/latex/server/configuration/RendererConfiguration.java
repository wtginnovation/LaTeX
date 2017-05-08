package de.vsfexperts.latex.server.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.vsfexperts.latex.renderer.LatexRenderer;
import de.vsfexperts.latex.renderer.MultithreadedLatexRenderer;
import de.vsfexperts.latex.storage.Archive;
import de.vsfexperts.latex.storage.FileSystemArchive;
import de.vsfexperts.latex.storage.LocalFileSystemStorage;

@Configuration
@ConfigurationProperties(prefix = "renderer")
public class RendererConfiguration {

	@Value("${renderer.workDirectory:#{systemProperties['java.io.tmpdir']}}")
	private String workDirectory;

	@Value("${renderer.archiveDirectory:#{systemProperties['java.io.tmpdir'] + T(java.io.File).separatorChar + 'archive'}}")
	private String archiveDirectory;

	@Value("${renderer.threads:#{T(java.lang.Runtime).runtime.availableProcessors()}}")
	private int parallelThreads;

	@Bean
	@Autowired
	public LatexRenderer latexRenderer(final LocalFileSystemStorage tempStorage, final Archive archive,
			final Executor executor) {
		return new MultithreadedLatexRenderer(tempStorage, archive, executor);
	}

	@Bean
	public Executor executor() {
		return Executors.newFixedThreadPool(parallelThreads);
	}

	@Bean
	public Archive archive() {
		return new FileSystemArchive(new LocalFileSystemStorage(archiveDirectory));
	}

	@Bean
	public LocalFileSystemStorage tempStorage() {
		return new LocalFileSystemStorage(workDirectory);
	}

	public String getWorkDirectory() {
		return workDirectory;
	}

	public void setWorkDirectory(final String workDirectory) {
		this.workDirectory = workDirectory;
	}

	public String getArchiveDirectory() {
		return archiveDirectory;
	}

	public void setArchiveDirectory(final String archiveDirectory) {
		this.archiveDirectory = archiveDirectory;
	}

}

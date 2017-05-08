package de.vsfexperts.latex.template;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

public final class ClasspathUtils {

	private ClasspathUtils() {
		throw new IllegalStateException("do not instantiate");
	}

	public static String getContent(final String classpathLocation) throws IOException {
		return IOUtils.toString(new ClassPathResource(classpathLocation).getInputStream(), Charset.forName("UTF-8"));
	}

}

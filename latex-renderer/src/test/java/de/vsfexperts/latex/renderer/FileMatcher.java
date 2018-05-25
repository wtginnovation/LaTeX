package de.vsfexperts.latex.renderer;

import static org.hamcrest.core.IsNot.not;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * File exists and is empty
 */
public class FileMatcher extends BaseMatcher<File> {

	public static Matcher<File> emptyFile() {
		return new FileMatcher();
	}

	public static Matcher<File> notAnEmptyFile() {
		return not(new FileMatcher());
	}

	@Override
	public boolean matches(final Object item) {
		if (item == null) {
			return false;
		}

		final File file = (File) item;

		return file.exists() && file.isFile() && FileUtils.sizeOf(file) == 0;
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("an empty file");
	}

}

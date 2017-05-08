package de.vsfexperts.latex.template;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Key value based context to pass parameters to template engine
 */
public class LatexContext {

	private final Map<String, Object> context = new HashMap<>();

	public LatexContext add(final String key, final Object value) {
		requireNonNull(key, "key must not be null");
		context.put(key, value);
		return this;
	}

	public Set<String> keys() {
		return Collections.unmodifiableSet(context.keySet());
	}

	public Map<String, Object> asMap() {
		return new HashMap<>(context);
	}

}

package nextmethod.web.razor.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public final class SimpleMarkupBuilder {

	public static SimpleMarkupBuilder create(final String name) {
		return new SimpleMarkupBuilder(name);
	}

	private final String name;
	private final Map<String, String> attributes;
	private final List<SimpleMarkupBuilder> children;

	private SimpleMarkupBuilder(final String name) {
		this.name = name;
		this.attributes = Maps.newHashMap();
		this.children = Lists.newArrayList();
	}

	public SimpleMarkupBuilder attribute(final String name, final String value) {
		this.attributes.put(name, value);
		return this;
	}

	public SimpleMarkupBuilder addChild(final SimpleMarkupBuilder child) {
		this.children.add(child);
		return this;
	}

	public SimpleMarkupBuilder newChild(final String name) {
		final SimpleMarkupBuilder child = new SimpleMarkupBuilder(name);
		this.children.add(child);
		return child;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("<")
				.append(this.name);

		for (String s : attributes.keySet()) {
			sb.append(" ").append(s).append("=\"").append(attributes.get(s)).append("\"");
		}
		sb.append(">");

		for (SimpleMarkupBuilder child : children) {
			sb.append(child.toString());
		}

		return sb.append("</").append(this.name).append(">").toString();
	}
}

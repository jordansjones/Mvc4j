package nextmethod.web.razor.tokenizer;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import nextmethod.base.Strings;
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;

import javax.annotation.Nullable;
import java.util.Map;

final class JavaKeywordDetector {

	private JavaKeywordDetector() {}

	private static final Map<String, JavaKeyword> keywords = createKeywordsMap();

	public static Optional<JavaKeyword> symbolTypeForIdentifier(@Nullable final String id) {
		if (Strings.isNullOrEmpty(id) || !keywords.containsKey(id)) return Optional.absent();
		return Optional.fromNullable(keywords.get(id));
	}

	private static Map<String, JavaKeyword> createKeywordsMap() {
		final ImmutableMap.Builder<String, JavaKeyword> builder = ImmutableMap.<String, JavaKeyword>builder();

		for (JavaKeyword keyword : JavaKeyword.values()) {
			builder.put(keyword.keyword(), keyword);
		}

		return builder.build();
	}
}

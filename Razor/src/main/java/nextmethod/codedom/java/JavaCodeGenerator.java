package nextmethod.codedom.java;

import com.google.common.collect.Maps;
import nextmethod.annotations.Internal;

import java.util.Map;

/**
 *
 */
// TODO
@Internal
public class JavaCodeGenerator/* extends CodeGenerator*/ {

	private Map<String, String> providerOptions = Maps.newHashMap();

	protected boolean dontWriteSemicolon;

	public JavaCodeGenerator() {
		dontWriteSemicolon = false;
	}

	public JavaCodeGenerator(final Map<String, String> providerOptions) {
		this.providerOptions.putAll(providerOptions);
	}

	protected Map<String, String> getProviderOptions() {
		return providerOptions;
	}

}

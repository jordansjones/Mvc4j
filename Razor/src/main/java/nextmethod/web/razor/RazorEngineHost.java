package nextmethod.web.razor;

import nextmethod.annotations.TODO;
import nextmethod.web.razor.generator.GeneratedClassContext;

import javax.annotation.Nonnull;

/**
 *
 */
@TODO
public class RazorEngineHost {

	protected boolean designTimeMode;
	protected boolean instrumentationActive;
	protected GeneratedClassContext generatedClassContext;

	public boolean isDesignTimeMode() {
		return designTimeMode;
	}

	public void setDesignTimeMode(boolean designTimeMode) {
		this.designTimeMode = designTimeMode;
	}

	public boolean enableInstrumentation() {
		return !isDesignTimeMode() && instrumentationActive;
	}

	public void enableInstrumentation(final boolean setActive) {
		this.instrumentationActive = setActive;
	}

	public GeneratedClassContext getGeneratedClassContext() {
		return generatedClassContext;
	}

	public void setGeneratedClassContext(@Nonnull final GeneratedClassContext generatedClassContext) {
		this.generatedClassContext = generatedClassContext;
	}
}

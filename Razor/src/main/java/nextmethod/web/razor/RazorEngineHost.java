package nextmethod.web.razor;

import com.google.common.collect.Sets;
import nextmethod.annotations.TODO;
import nextmethod.web.razor.generator.GeneratedClassContext;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

/**
 *
 */
@TODO
public class RazorEngineHost {

	static final String InternalDefaultClassName = "__CompiledTemplate";
	static final String InternalDefaultPackage = "Razor";

	protected boolean designTimeMode;
	protected boolean instrumentationActive;
	protected GeneratedClassContext generatedClassContext;
	protected String instrumentedSourceFilePath;
	protected final Set<String> packageImports;
	protected String defaultBaseClass;
	protected String defaultPackage;
	protected String defaultClassName;
	protected boolean staticHelpers;

	protected RazorEngineHost() {
		this.generatedClassContext = GeneratedClassContext.Default;
		this.packageImports = Sets.newHashSet();
		this.designTimeMode = false;
		this.defaultPackage = InternalDefaultPackage;
		this.defaultClassName = InternalDefaultClassName;
		this.enableInstrumentation(true);
	}

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

	public String getInstrumentedSourceFilePath() {
		return instrumentedSourceFilePath;
	}

	public void setInstrumentedSourceFilePath(final String instrumentedSourceFilePath) {
		this.instrumentedSourceFilePath = instrumentedSourceFilePath;
	}

	public Collection<String> getPackageImports() {
		return packageImports;
	}

	public String getDefaultBaseClass() {
		return defaultBaseClass;
	}

	public void setDefaultBaseClass(final String defaultBaseClass) {
		this.defaultBaseClass = defaultBaseClass;
	}

	public String getDefaultClassName() {
		return defaultClassName;
	}

	public void setDefaultClassName(final String defaultClassName) {
		this.defaultClassName = defaultClassName;
	}

	public String getDefaultPackage() {
		return defaultPackage;
	}

	public void setDefaultPackage(final String defaultPackage) {
		this.defaultPackage = defaultPackage;
	}

	public boolean isStaticHelpers() {
		return staticHelpers;
	}

	public void setStaticHelpers(final boolean staticHelpers) {
		this.staticHelpers = staticHelpers;
	}
}

package nextmethod.web.razor.generator;

import com.google.common.base.Strings;
import nextmethod.annotations.TODO;

@TODO
public class GeneratedClassContext {

	public static final String DefaultWriteMethodName = "Write";
	public static final String DefaultWriteLiteralMethodName = "WriteLiteral";
	public static final String DefaultExecuteMethodName = "Execute";
	public static final String DefaultLayoutPropertyName = "Layout";
	public static final String DefaultWriteAttributeMethodName = "WriteAttribute";
	public static final String DefaultWriteAttributeToMethodName = "WriteAttributeTo";

	public static final GeneratedClassContext Default = new GeneratedClassContext(DefaultExecuteMethodName, DefaultWriteMethodName, DefaultWriteLiteralMethodName);

	public GeneratedClassContext(final String executeMethodName, final String writeMethodName, final String writeLiteralMethodName) {

		this.writeMethodName = writeMethodName;
		this.writeLiteralMethodName = writeLiteralMethodName;
		this.executeMethodName = executeMethodName;

		this.writeToMethodName = null;
		this.writeLiteralToMethodName = null;
		this.templateTypeName = null;
		this.defineSectionMethodName = null;

		this.layoutPropertyName = DefaultLayoutPropertyName;
		this.writeAttributeMethodName = DefaultWriteAttributeMethodName;
		this.writeAttributeToMethodName = DefaultWriteAttributeToMethodName;
	}

	public boolean allowSections() {
		return !Strings.isNullOrEmpty(defineSectionMethodName);
	}

	public boolean allowTemplates() {
		return !Strings.isNullOrEmpty(templateTypeName);
	}

	public boolean supportsInstrumentation() {
		return !Strings.isNullOrEmpty(beginContextMethodName) && !Strings.isNullOrEmpty(endContextMethodName);
	}

	private String writeMethodName;
	private String writeLiteralMethodName;
	private String writeToMethodName;
	private String writeLiteralToMethodName;
	private String executeMethodName;

	public String getWriteMethodName() {
		return writeMethodName;
	}

	public String getWriteLiteralMethodName() {
		return writeLiteralMethodName;
	}

	public String getWriteToMethodName() {
		return writeToMethodName;
	}

	public String getWriteLiteralToMethodName() {
		return writeLiteralToMethodName;
	}

	public String getExecuteMethodName() {
		return executeMethodName;
	}

	// Optional items
	private String beginContextMethodName;
	private String endContextMethodName;
	private String layoutPropertyName;
	private String defineSectionMethodName;
	private String templateTypeName;
	private String writeAttributeMethodName;
	private String writeAttributeToMethodName;

	private String resolveUrlMethodName;


	public String getBeginContextMethodName() {
		return beginContextMethodName;
	}

	public void setBeginContextMethodName(String beginContextMethodName) {
		this.beginContextMethodName = beginContextMethodName;
	}

	public String getEndContextMethodName() {
		return endContextMethodName;
	}

	public void setEndContextMethodName(String endContextMethodName) {
		this.endContextMethodName = endContextMethodName;
	}

	public String getLayoutPropertyName() {
		return layoutPropertyName;
	}

	public void setLayoutPropertyName(String layoutPropertyName) {
		this.layoutPropertyName = layoutPropertyName;
	}

	public String getDefineSectionMethodName() {
		return defineSectionMethodName;
	}

	public void setDefineSectionMethodName(String defineSectionMethodName) {
		this.defineSectionMethodName = defineSectionMethodName;
	}

	public String getTemplateTypeName() {
		return templateTypeName;
	}

	public void setTemplateTypeName(String templateTypeName) {
		this.templateTypeName = templateTypeName;
	}

	public String getWriteAttributeMethodName() {
		return writeAttributeMethodName;
	}

	public void setWriteAttributeMethodName(String writeAttributeMethodName) {
		this.writeAttributeMethodName = writeAttributeMethodName;
	}

	public String getWriteAttributeToMethodName() {
		return writeAttributeToMethodName;
	}

	public void setWriteAttributeToMethodName(String writeAttributeToMethodName) {
		this.writeAttributeToMethodName = writeAttributeToMethodName;
	}

	public String getResolveUrlMethodName() {
		return resolveUrlMethodName;
	}

	public void setResolveUrlMethodName(String resolveUrlMethodName) {
		this.resolveUrlMethodName = resolveUrlMethodName;
	}
}

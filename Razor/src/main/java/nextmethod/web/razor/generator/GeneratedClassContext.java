package nextmethod.web.razor.generator;

import nextmethod.base.Strings;

import java.util.Objects;

import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.common.Mvc4jCommonResources.CommonResources;

// TODO
public class GeneratedClassContext {

	public static final String DefaultWriteMethodName = "Write";
	public static final String DefaultWriteLiteralMethodName = "WriteLiteral";
	public static final String DefaultExecuteMethodName = "Execute";
	public static final String DefaultLayoutPropertyName = "Layout";
	public static final String DefaultWriteAttributeMethodName = "WriteAttribute";
	public static final String DefaultWriteAttributeToMethodName = "WriteAttributeTo";

	public static final GeneratedClassContext Default = new GeneratedClassContext(DefaultExecuteMethodName, DefaultWriteMethodName, DefaultWriteLiteralMethodName);

	public GeneratedClassContext(final String executeMethodName, final String writeMethodName, final String writeLiteralMethodName) {

		if (Strings.isNullOrEmpty(executeMethodName)) {
			throw new IllegalArgumentException(String.format(
				CommonResources().argumentCannotBeNullOrEmpty(),
				"executeMethodName"
			));
		}
		if (Strings.isNullOrEmpty(writeMethodName)) {
			throw new IllegalArgumentException(String.format(
				CommonResources().argumentCannotBeNullOrEmpty(),
				"writeMethodName"
			));
		}
		if (Strings.isNullOrEmpty(writeLiteralMethodName)) {
			throw new IllegalArgumentException(String.format(
				CommonResources().argumentCannotBeNullOrEmpty(),
				"writeLiteralMethodName"
			));
		}

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

	public GeneratedClassContext(
		final String executeMethodName,
	    final String writeMethodName,
	    final String writeLiteralMethodName,
	    final String writeToMethodName,
	    final String writeLiteralToMethodName,
	    final String templateTypeName
	) {
		this(executeMethodName, writeMethodName, writeLiteralMethodName);

		this.writeToMethodName = writeToMethodName;
		this.writeLiteralToMethodName = writeLiteralToMethodName;
		this.templateTypeName = templateTypeName;
	}

	public GeneratedClassContext(
		final String executeMethodName,
		final String writeMethodName,
		final String writeLiteralMethodName,
		final String writeToMethodName,
		final String writeLiteralToMethodName,
		final String templateTypeName,
	    final String defineSectionMethodName
	) {
		this(executeMethodName, writeMethodName, writeLiteralMethodName, writeToMethodName, writeLiteralToMethodName, templateTypeName);

		this.defineSectionMethodName = defineSectionMethodName;
	}

	public GeneratedClassContext(
		final String executeMethodName,
		final String writeMethodName,
		final String writeLiteralMethodName,
		final String writeToMethodName,
		final String writeLiteralToMethodName,
		final String templateTypeName,
		final String defineSectionMethodName,
	    final String beginContextMethodName,
	    final String endContextMethodName
	) {
		this(executeMethodName, writeMethodName, writeLiteralMethodName, writeToMethodName, writeLiteralToMethodName, templateTypeName, defineSectionMethodName);

		this.beginContextMethodName = beginContextMethodName;
		this.endContextMethodName = endContextMethodName;
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

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		final GeneratedClassContext other = typeAs(obj, GeneratedClassContext.class);
		if (other == null) {
			return false;
		}

		return Objects.equals(defineSectionMethodName, other.defineSectionMethodName)
			&& Objects.equals(writeMethodName, other.writeMethodName)
			&& Objects.equals(writeLiteralMethodName, other.writeLiteralMethodName)
			&& Objects.equals(writeToMethodName, other.writeToMethodName)
			&& Objects.equals(writeLiteralToMethodName, other.writeLiteralToMethodName)
			&& Objects.equals(executeMethodName, other.executeMethodName)
			&& Objects.equals(templateTypeName, other.templateTypeName)
			&& Objects.equals(beginContextMethodName, other.beginContextMethodName)
			&& Objects.equals(endContextMethodName, other.endContextMethodName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			defineSectionMethodName,
			writeMethodName,
			writeLiteralMethodName,
			writeToMethodName,
			writeLiteralToMethodName,
			executeMethodName,
			templateTypeName,
			beginContextMethodName,
			endContextMethodName
		);
	}

	public static boolean isEqualTo(final GeneratedClassContext left, final GeneratedClassContext right) {
		return left.equals(right);
	}

	public static boolean isNotEqualTo(final GeneratedClassContext left, final GeneratedClassContext right) {
		return !isEqualTo(left, right);
	}
}

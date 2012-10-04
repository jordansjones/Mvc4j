package nextmethod.codedom;

import javax.annotation.Nonnull;
import java.util.EnumSet;

// TODO
public class CodeTypeMember extends CodeObject {

	private String name;
	private EnumSet<MemberAttributes> attributes;
	private CodeAnnotationDeclarationCollection customAttributes;
	private CodeLinePragma linePragma;

	public CodeTypeMember() {
		this.attributes = MemberAttributes.setOf(MemberAttributes.Private, MemberAttributes.Final);
	}

	@Nonnull
	public EnumSet<MemberAttributes> getAttributes() {
		return attributes;
	}

	public void setAttributes(@Nonnull final EnumSet<MemberAttributes> attributes) {
		this.attributes = attributes;
	}

	public void setAttributes(final MemberAttributes... attributes) {
		setAttributes(MemberAttributes.setOf(attributes));
	}

	public String getName() {
		return name == null ? "" : name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public CodeLinePragma getLinePragma() {
		return linePragma;
	}

	public void setLinePragma(final CodeLinePragma linePragma) {
		this.linePragma = linePragma;
	}

	public CodeAnnotationDeclarationCollection getCustomAttributes() {
		if (customAttributes == null) {
			customAttributes = new CodeAnnotationDeclarationCollection();
		}
		return customAttributes;
	}

	public void setCustomAttributes(final CodeAnnotationDeclarationCollection customAttributes) {
		this.customAttributes = customAttributes;
	}
}

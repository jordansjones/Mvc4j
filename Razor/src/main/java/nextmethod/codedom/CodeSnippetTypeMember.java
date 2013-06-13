package nextmethod.codedom;

import nextmethod.base.Strings;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class CodeSnippetTypeMember extends CodeTypeMember implements Serializable {

	private static final long serialVersionUID = 7331691969139845195L;

	private String text;

	public CodeSnippetTypeMember() {
	}

	public CodeSnippetTypeMember(final String text) {
		this.text = text;
	}

	public String getText() {
		if (text == null) {
			return Strings.Empty;
		}
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	@Override
	public void accept(@Nonnull final ICodeDomVisitor visitor) {
		visitor.visit(this);
	}
}

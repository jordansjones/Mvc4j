package nextmethod.codedom;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collections;

/**
 *
 */
public class CodeArrayCreateExpression extends CodeExpression implements Serializable {

	private static final long serialVersionUID = -4177872714495683987L;

	private CodeTypeReference createType;
	private CodeExpressionCollection initializers;
	private CodeExpression sizeExpression;
	private int size;

	// region Constructors

	public CodeArrayCreateExpression() {
	}

	public CodeArrayCreateExpression(final CodeTypeReference createType, final CodeExpression size) {
		this.createType = createType;
		this.sizeExpression = size;
	}

	public CodeArrayCreateExpression(final CodeTypeReference createType, final CodeExpression... initializers) {
		this.createType = createType;
		if (initializers != null && initializers.length > 0) {
			Collections.addAll(this.getInitializers(), initializers);
		}
	}

	public CodeArrayCreateExpression(final CodeTypeReference createType, final int size) {
		this.createType = createType;
		this.size = size;
	}

	public CodeArrayCreateExpression(final String createType, final CodeExpression size) {
		this.createType = new CodeTypeReference(createType);
		this.sizeExpression = size;
	}

	public CodeArrayCreateExpression(final String createType, final CodeExpression... initializers) {
		this.createType = new CodeTypeReference(createType);
		if (initializers != null && initializers.length > 0) {
			Collections.addAll(this.getInitializers(), initializers);
		}
	}

	public CodeArrayCreateExpression(final String createType, final int size) {
		this.createType = new CodeTypeReference(createType);
		this.size = size;
	}

	public CodeArrayCreateExpression(final Class<?> createType, final CodeExpression size) {
		this.createType = new CodeTypeReference(createType);
		this.sizeExpression = size;
	}

	public CodeArrayCreateExpression(final Class<?> createType, final CodeExpression... initializers) {
		this.createType = new CodeTypeReference(createType);
		if (initializers != null && initializers.length > 0) {
			Collections.addAll(this.getInitializers(), initializers);
		}
	}

	public CodeArrayCreateExpression(final Class<?> createType, final int size) {
		this.createType = new CodeTypeReference(createType);
		this.size = size;
	}


	// endregion

	// region Properties

	public CodeTypeReference getCreateType() {
		if (createType == null) {
			createType = new CodeTypeReference(Void.class);
		}
		return createType;
	}

	public void setCreateType(final CodeTypeReference createType) {
		this.createType = createType;
	}

	public CodeExpressionCollection getInitializers() {
		if (initializers == null) {
			initializers = new CodeExpressionCollection();
		}
		return initializers;
	}

	public CodeExpression getSizeExpression() {
		return sizeExpression;
	}

	public void setSizeExpression(final CodeExpression sizeExpression) {
		this.sizeExpression = sizeExpression;
	}

	public int getSize() {
		return size;
	}

	public void setSize(final int size) {
		this.size = size;
	}

	// endregion


	@Override
	public void accept(@Nonnull final ICodeDomVisitor visitor) {
		visitor.visit(this);
	}
}

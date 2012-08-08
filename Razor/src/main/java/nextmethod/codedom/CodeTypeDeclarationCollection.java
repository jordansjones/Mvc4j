package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import nextmethod.annotations.TODO;

import java.io.Serializable;
import java.util.List;

@TODO
public class CodeTypeDeclarationCollection extends ForwardingList<CodeTypeDeclaration> implements Serializable {

	private static final long serialVersionUID = 772049652319814925L;

	private final List<CodeTypeDeclaration> listDelegate;

	public CodeTypeDeclarationCollection() {
		this.listDelegate = Lists.newArrayList();
	}

	@Override
	protected List<CodeTypeDeclaration> delegate() {
		return this.listDelegate;
	}
}

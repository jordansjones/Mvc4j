package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import nextmethod.annotations.TODO;

import java.io.Serializable;
import java.util.List;

@TODO
public class CodeAnnotationDeclarationCollection extends ForwardingList<CodeAnnotationDeclaration> implements Serializable {

	private static final long serialVersionUID = -1312001665557975434L;

	private final List<CodeAnnotationDeclaration> listDelegate;

	public CodeAnnotationDeclarationCollection() {
		this.listDelegate = Lists.newArrayList();
	}

	@Override
	protected List<CodeAnnotationDeclaration> delegate() {
		return listDelegate;
	}
}

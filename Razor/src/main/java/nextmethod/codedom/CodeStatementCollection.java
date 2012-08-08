package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import nextmethod.annotations.TODO;

import java.io.Serializable;
import java.util.List;

@TODO
public class CodeStatementCollection extends ForwardingList<CodeStatement> implements Serializable {

	private static final long serialVersionUID = -1535317559743163232L;

	private final List<CodeStatement> listDelegate;

	public CodeStatementCollection() {
		this.listDelegate = Lists.newArrayList();
	}

	@Override
	protected List<CodeStatement> delegate() {
		return this.listDelegate;
	}
}

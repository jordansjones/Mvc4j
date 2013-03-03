package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

// TODO
public class CodeAnnotationArgumentCollection extends ForwardingList<CodeAnnotationArgument> implements Serializable {

	private static final long serialVersionUID = -1764675433348799227L;

	private final List<CodeAnnotationArgument> listDelegate;

	public CodeAnnotationArgumentCollection() {
		this.listDelegate = Lists.newArrayList();
	}

	@Override
	protected List<CodeAnnotationArgument> delegate() {
		return listDelegate;
	}
}

package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import nextmethod.annotations.TODO;

import java.io.Serializable;
import java.util.List;

@TODO
public class CodeTypeReferenceCollection extends ForwardingList<CodeTypeReference> implements Serializable {

	private static final long serialVersionUID = 960552001815328968L;

	private final List<CodeTypeReference> listDelegate;

	public CodeTypeReferenceCollection() {
		this.listDelegate = Lists.newArrayList();
	}

	@Override
	protected List<CodeTypeReference> delegate() {
		return listDelegate;
	}
}

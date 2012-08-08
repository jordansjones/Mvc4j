package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import nextmethod.annotations.TODO;

import java.io.Serializable;
import java.util.List;

@TODO
public class CodeTypeMemberCollection extends ForwardingList<CodeTypeMember> implements Serializable {

	private static final long serialVersionUID = -931048809114327031L;

	private final List<CodeTypeMember> listDelegate;

	public CodeTypeMemberCollection() {
		this.listDelegate = Lists.newArrayList();
	}

	@Override
	protected List<CodeTypeMember> delegate() {
		return this.listDelegate;
	}
}

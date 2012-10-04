package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

// TODO
public class CodePackageCollection extends ForwardingList<CodePackage> implements Serializable {

	private static final long serialVersionUID = 3167207761604916491L;

	private final List<CodePackage> listDelegate;

	public CodePackageCollection() {
		this.listDelegate = Lists.newArrayList();
	}

	@Override
	protected List<CodePackage> delegate() {
		return this.listDelegate;
	}
}

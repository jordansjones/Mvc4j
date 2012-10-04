package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

// TODO
public class CodePackageImportCollection extends ForwardingList<CodePackageImport> implements Serializable {

	private static final long serialVersionUID = -3609814966958487242L;

	private final List<CodePackageImport> listDelegate;

	public CodePackageImportCollection() {
		this.listDelegate = Lists.newArrayList();
	}

	@Override
	protected List<CodePackageImport> delegate() {
		return listDelegate;
	}
}

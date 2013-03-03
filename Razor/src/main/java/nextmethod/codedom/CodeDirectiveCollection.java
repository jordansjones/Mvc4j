package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import nextmethod.base.NotImplementedException;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
// TODO
public class CodeDirectiveCollection extends ForwardingList<CodeDirective> implements Serializable {

	private static final long serialVersionUID = -8075639131015205715L;

	@Override
	protected List<CodeDirective> delegate() {
		throw new NotImplementedException();
	}
}

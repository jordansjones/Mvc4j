package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import nextmethod.base.NotImplementedException;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
// TODO
public class CodeCommentStatementCollection extends ForwardingList<CodeCommentStatement> implements Serializable {

	private static final long serialVersionUID = 3341940904493109847L;

	@Override
	protected List<CodeCommentStatement> delegate() {
		throw new NotImplementedException();
	}
}

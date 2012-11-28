package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import nextmethod.base.NotImplementedException;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
// TODO
public class CodeExpressionCollection extends ForwardingList<CodeExpression> implements Serializable {

	private static final long serialVersionUID = 5724984308676023105L;

	@Override
	protected List<CodeExpression> delegate() {
		throw new NotImplementedException();
	}
}

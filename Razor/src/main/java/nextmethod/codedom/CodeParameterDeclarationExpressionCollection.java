package nextmethod.codedom;

import com.google.common.collect.ForwardingList;
import nextmethod.base.NotImplementedException;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
// TODO
public class CodeParameterDeclarationExpressionCollection extends ForwardingList<CodeParameterDeclarationExpression> implements Serializable {

	private static final long serialVersionUID = 8426854163696281399L;

	@Override
	protected List<CodeParameterDeclarationExpression> delegate() {
		throw new NotImplementedException();
	}
}

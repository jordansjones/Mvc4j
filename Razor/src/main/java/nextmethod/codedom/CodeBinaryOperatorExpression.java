/*
 * Copyright 2013 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.codedom;

import nextmethod.annotations.Internal;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 *
 */
public class CodeBinaryOperatorExpression extends CodeExpression implements Serializable {

	private static final long serialVersionUID = -4015788666812764656L;

	private CodeExpression left, right;
	private CodeBinaryOperatorType op;

	public CodeBinaryOperatorExpression() {
	}

	public CodeBinaryOperatorExpression(final CodeExpression left, final CodeBinaryOperatorType op, final CodeExpression right) {
		this.left = left;
		this.op = op;
		this.right = right;
	}

	public CodeExpression getLeft() {
		return left;
	}

	public void setLeft(final CodeExpression left) {
		this.left = left;
	}

	public CodeBinaryOperatorType getOp() {
		return op;
	}

	public void setOp(final CodeBinaryOperatorType op) {
		this.op = op;
	}

	public CodeExpression getRight() {
		return right;
	}

	public void setRight(final CodeExpression right) {
		this.right = right;
	}

	@Override
	@Internal
	public void accept(@Nonnull final ICodeDomVisitor visitor) {
		visitor.visit(this);
	}
}

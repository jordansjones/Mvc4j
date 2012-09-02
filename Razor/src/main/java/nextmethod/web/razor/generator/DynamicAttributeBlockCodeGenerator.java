package nextmethod.web.razor.generator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import nextmethod.base.Delegates;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static nextmethod.base.TypeHelpers.typeAs;

public class DynamicAttributeBlockCodeGenerator extends BlockCodeGenerator {

	private static final String ValueWriterName = "__razor_attribute_value_writer";

	private String oldTargetWriter;
	private boolean isExpression;
	private ExpressionRenderingMode oldRenderingMode;

	private final LocationTagged<String> prefix;
	private final SourceLocation valueStart;

	public DynamicAttributeBlockCodeGenerator(@Nonnull final LocationTagged<String> prefix, final int offset, final int line, final int col) {
		this(prefix, new SourceLocation(offset, line, col));
	}

	public DynamicAttributeBlockCodeGenerator(@Nonnull final LocationTagged<String> prefix, @Nonnull final SourceLocation valueStart) {
		this.prefix = prefix;
		this.valueStart = valueStart;
	}

	@Override
	public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		if (context.getHost().isDesignTimeMode()) {
			return;
		}

		// Whate kind of block is nested within
		final String generatedCode;
		final Block child = (Block) Iterables.find(target.getChildren(), isBlockPredicate, null);
		if (child != null && child.getType() == BlockType.Expression) {
			isExpression = true;
			generatedCode = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
				@Override
				public void invoke(@Nullable final CodeWriter input) {
					assert input != null;

					input.writeParameterSeparator();
					input.writeStartMethodInvoke("Tuple.Create");
					input.writeLocationTaggedString(prefix);
					input.writeParameterSeparator();
					input.writeStartMethodInvoke("Tuple.Create", "java.lang.Object", "java.lang.Integer");
				}
			});

			oldRenderingMode = context.getExpressionRenderingMode();
			context.setExpressionRenderingMode(ExpressionRenderingMode.InjectCode);
		}
		else {
			generatedCode = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
				@Override
				public void invoke(@Nullable final CodeWriter input) {
					assert input != null;

					input.writeParameterSeparator();
					input.writeStartMethodInvoke("Tuple.Create");
					input.writeLocationTaggedString(prefix);
					input.writeParameterSeparator();
					input.writeStartMethodInvoke("Tuple.Create", "java.lang.Object", "java.lang.Integer");
					input.writeStartConstructor(context.getHost().getGeneratedClassContext().getTemplateTypeName());
					input.writeStartLambdaDelegate(ValueWriterName);
				}
			});
		}

		context.markEndOfGeneratedCode();
		context.bufferStatementFragment(generatedCode);

		oldTargetWriter = context.getTargetWriterName();
		context.setTargetWriterName(ValueWriterName);
	}

	@Override
	public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		if (context.getHost().isDesignTimeMode()) {
			return;
		}

		final String generatedCode;
		if (isExpression) {
			generatedCode = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
				@Override
				public void invoke(@Nullable final CodeWriter input) {
					assert input != null;

					input.writeParameterSeparator();
					input.writeSnippet(String.valueOf(valueStart.getAbsoluteIndex()));
					input.writeEndMethodInvoke();
					input.writeParameterSeparator();
					// This attribute value is not a literal value, it is dynamically generated
					input.writeBooleanLiteral(false);
					input.writeEndMethodInvoke();
					input.writeLineContinuation();
				}
			});
			context.setExpressionRenderingMode(oldRenderingMode);
		}
		else {
			generatedCode = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
				@Override
				public void invoke(@Nullable final CodeWriter input) {
					assert input != null;

					input.writeEndLambdaDelegate();
					input.writeEndConstructor();
					input.writeParameterSeparator();
					input.writeSnippet(String.valueOf(valueStart.getAbsoluteIndex()));
					input.writeEndMethodInvoke();
					input.writeParameterSeparator();
					// This attribute value is not a literal value, it is dynamically generated
					input.writeBooleanLiteral(false);
					input.writeEndMethodInvoke();
					input.writeLineContinuation();
				}
			});
		}

		context.addStatement(generatedCode);
		context.setTargetWriterName(oldTargetWriter);
	}

	public LocationTagged<String> getPrefix() {
		return prefix;
	}

	public SourceLocation getValueStart() {
		return valueStart;
	}

	@Override
	public String toString() {
		return String.format("DynAttr:%s", prefix);
	}

	@Override
	public boolean equals(final Object obj) {
		final DynamicAttributeBlockCodeGenerator other = typeAs(obj, DynamicAttributeBlockCodeGenerator.class);
		return other != null
			&& Objects.equals(other.prefix, prefix);
	}

	@Override
	public int hashCode() {
		return Objects.hash(prefix);
	}

	private static final Predicate<SyntaxTreeNode> isBlockPredicate = new Predicate<SyntaxTreeNode>() {
		@Override
		public boolean apply(@Nullable final SyntaxTreeNode input) {
			return input != null && input.isBlock();
		}
	};
}

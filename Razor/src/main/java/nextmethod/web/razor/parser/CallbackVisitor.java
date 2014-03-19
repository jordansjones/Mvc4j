package nextmethod.web.razor.parser;

import nextmethod.threading.SendOrPostCallback;
import nextmethod.threading.SynchronizationContext;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static nextmethod.base.Delegates.IAction;
import static nextmethod.base.Delegates.IAction1;

public class CallbackVisitor extends ParserVisitor {

	private final IAction1<Span> spanCallback;
	private final IAction1<RazorError> errorCallback;
	private final IAction1<BlockType> endBlockCallback;
	private final IAction1<BlockType> startBlockCallback;
	private final IAction completeCallback;

	private SynchronizationContext synchronizationContext;

	public CallbackVisitor(@Nonnull final IAction1<Span> spanCallback) {
		this(spanCallback, input -> {
		});
	}

	public CallbackVisitor(@Nonnull final IAction1<Span> spanCallback, @Nonnull final IAction1<RazorError> errorCallback) {
		this(spanCallback, errorCallback,
			input -> {
			},
			input -> {
			}
		);
	}

	public CallbackVisitor(@Nonnull final IAction1<Span> spanCallback, @Nonnull final IAction1<RazorError> errorCallback, @Nonnull final IAction1<BlockType> startBlockCallback, @Nonnull final IAction1<BlockType> endBlockCallback) {
		this(spanCallback, errorCallback, startBlockCallback, endBlockCallback, () -> {
		});
	}

	public CallbackVisitor(@Nonnull final IAction1<Span> spanCallback, @Nonnull final IAction1<RazorError> errorCallback, @Nonnull final IAction1<BlockType> startBlockCallback, @Nonnull final IAction1<BlockType> endBlockCallback, @Nonnull final IAction completeCallback) {
		this.spanCallback = spanCallback;
		this.errorCallback = errorCallback;
		this.startBlockCallback = startBlockCallback;
		this.endBlockCallback = endBlockCallback;
		this.completeCallback = completeCallback;
	}

	@Override
	public void visitStartBlock(@Nonnull final Block block) {
		super.visitStartBlock(block);
		raiseCallback(synchronizationContext, block.getType(), startBlockCallback);
	}

	@Override
	public void visitSpan(@Nonnull final Span span) {
		super.visitSpan(span);
		raiseCallback(synchronizationContext, span, spanCallback);
	}

	@Override
	public void visitEndBlock(@Nonnull final Block block) {
		super.visitEndBlock(block);
		raiseCallback(synchronizationContext, block.getType(), endBlockCallback);
	}

	@Override
	public void visitError(@Nonnull final RazorError error) {
		super.visitError(error);
		raiseCallback(synchronizationContext, error, errorCallback);
	}

	@Override
	public void onComplete() {
		super.onComplete();
		raiseCallback(synchronizationContext, Object.class.cast(null), input -> completeCallback.invoke());
	}

	private static <T> void raiseCallback(final SynchronizationContext syncContext, final T param, final IAction1<T> callback) {
		if (callback != null) {
			if (syncContext != null) {
				final SendOrPostCallback sendOrPostCallback = new SendOrPostCallback() {
					@SuppressWarnings("unchecked")
					@Override
					public void invoke(@Nullable final Object input) {
						callback.invoke((T) input);
					}
				};
				syncContext.post(sendOrPostCallback, param);
			}
			else {
				callback.invoke(param);
			}
		}
	}

	public SynchronizationContext getSynchronizationContext() {
		return synchronizationContext;
	}

	public CallbackVisitor setSynchronizationContext(final SynchronizationContext synchronizationContext) {
		this.synchronizationContext = synchronizationContext;
		return this;
	}
}

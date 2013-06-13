package nextmethod.web.razor.parser;

import nextmethod.base.Delegates;
import nextmethod.base.OutParam;
import nextmethod.threading.SendOrPostCallback;
import nextmethod.threading.SynchronizationContext;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.FunctionsBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.annotation.Nullable;

import static nextmethod.base.TypeHelpers.typeAs;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class CallbackParserListenerTest {

	@Test
	public void listenerConstructedWithSpanCallbackCallsCallbackOnEndSpan() {
		runOnEndSpanTest(new Delegates.IFunc1<Delegates.IAction1<Span>, CallbackVisitor>() {
			@Override
			public CallbackVisitor invoke(@Nullable final Delegates.IAction1<Span> callback) {
				assert callback != null;
				return new CallbackVisitor(callback);
			}
		});
	}

	@Test
	public void listenerConstructedWithSpanCallbackDoesNotThrowOnStartBlockEndBlockOrError() {
		final Delegates.IAction1<Span> spanCallback = createNopAction();

		final CallbackVisitor listener = new CallbackVisitor(spanCallback);

		listener.visitStartBlock(new FunctionsBlock());
		listener.visitError(new RazorError("Error", SourceLocation.Zero));
		listener.visitEndBlock(new FunctionsBlock());
	}

	@Test
	public void listenerConstructedWithSpanAndErrorCallbackCallsCallbackOnEndSpan() {
		runOnEndSpanTest(new Delegates.IFunc1<Delegates.IAction1<Span>, CallbackVisitor>() {
			@Override
			public CallbackVisitor invoke(@Nullable final Delegates.IAction1<Span> input1) {
				assert input1 != null;
				return new CallbackVisitor(
					input1,
					createNopAction(RazorError.class)
				);
			}
		});
	}

	@Test
	public void listenerConstructedWithSpanAndErrorCallbackCallsCallbackOnError() {
		runOnErrorTest(new Delegates.IFunc1<Delegates.IAction1<RazorError>, CallbackVisitor>() {
			@Override
			public CallbackVisitor invoke(@Nullable final Delegates.IAction1<RazorError> input1) {
				assert input1 != null;
				return new CallbackVisitor(
					createNopAction(Span.class),
					input1
				);
			}
		});
	}

	@Test
	public void listenerConstructedWithAllCallbacksCallsCallbackOnEndSpan() {
		runOnEndSpanTest(
			new Delegates.IFunc1<Delegates.IAction1<Span>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<Span> input1) {
					assert input1 != null;
					return new CallbackVisitor(
						input1,
						createNopAction(RazorError.class),
						createNopAction(BlockType.class),
						createNopAction(BlockType.class)
					);
				}
			}
		);
	}

	@Test
	public void listenerConstructedWithAllCallbacksCallsCallbackOnError() {
		runOnErrorTest(
			new Delegates.IFunc1<Delegates.IAction1<RazorError>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<RazorError> input1) {
					assert input1 != null;
					return new CallbackVisitor(
						createNopAction(Span.class),
						input1,
						createNopAction(BlockType.class),
						createNopAction(BlockType.class)
					);
				}
			}
		);
	}

	@Test
	public void listenerConstructedWithAllCallbacksCallsCallbackOnStartBlock() {
		runOnStartBlockTest(
			new Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<BlockType> input1) {
					assert input1 != null;
					return new CallbackVisitor(
						createNopAction(Span.class),
						createNopAction(RazorError.class),
						input1,
						createNopAction(BlockType.class)
					);
				}
			}
		);
	}

	@Test
	public void listenerConstructedWithAllCallbacksCallsCallbackOnEndBlock() {
		runOnEndBlockTest(
			new Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<BlockType> input1) {
					assert input1 != null;
					return new CallbackVisitor(
						createNopAction(Span.class),
						createNopAction(RazorError.class),
						createNopAction(BlockType.class),
						input1
					);
				}
			}
		);
	}

	@Test
	public void listenerCallsOnEndSpanCallbackUsingSynchronizationContextIfSpecified() {
		runSyncContextTest(
			new SpanBuilder().build(),
			new SpanBuilder().build(),
			new Delegates.IFunc1<Delegates.IAction1<Span>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<Span> spanCallback) {
					assert spanCallback != null;
					return new CallbackVisitor(
						spanCallback,
						createNopAction(RazorError.class),
						createNopAction(BlockType.class),
						createNopAction(BlockType.class)
					);
				}
			},
			new Delegates.IAction2<CallbackVisitor, Span>() {
				@Override
				public void invoke(@Nullable final CallbackVisitor listener, @Nullable final Span expected) {
					assert listener != null;
					assert expected != null;
					listener.visitSpan(expected);
				}
			}
		);
	}

	@Test
	public void listenerCallsOnStartBlockCallbackUsingSynchronizationContextIfSpecified() {
		runSyncContextTest(
			BlockType.values()[0],
			BlockType.Template,
			new Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<BlockType> startBlockCall) {
					assert startBlockCall != null;
					return new CallbackVisitor(
						createNopAction(Span.class),
						createNopAction(RazorError.class),
						startBlockCall,
						createNopAction(BlockType.class)
					);
				}
			},
			new Delegates.IAction2<CallbackVisitor, BlockType>() {
				@Override
				public void invoke(@Nullable final CallbackVisitor listener, @Nullable final BlockType expected) {
					assert listener != null;
					listener.visitStartBlock(new BlockBuilder().setType(expected).build());
				}
			}
		);
	}

	@Test
	public void listenerCallsOnEndBlockCallbackUsingSynchronizationContextIfSpecified() {
		runSyncContextTest(
			BlockType.values()[0],
			BlockType.Template,
			new Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<BlockType> endBlockCallback) {
					assert endBlockCallback != null;
					return new CallbackVisitor(
						createNopAction(Span.class),
						createNopAction(RazorError.class),
						createNopAction(BlockType.class),
						endBlockCallback
					);
				}
			},
			new Delegates.IAction2<CallbackVisitor, BlockType>() {
				@Override
				public void invoke(@Nullable final CallbackVisitor listener, @Nullable final BlockType expected) {
					assert listener != null;
					listener.visitEndBlock(new BlockBuilder().setType(expected).build());
				}
			}
		);
	}

	@Test
	public void listenerCallsOnErrorCallbackUsingSynchronizationContextIfSpecified() {
		runSyncContextTest(
			new RazorError("Foo", SourceLocation.Zero),
			new RazorError("Bar", 42, 42, 42),
			new Delegates.IFunc1<Delegates.IAction1<RazorError>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<RazorError> errorCallback) {
					assert errorCallback != null;
					return new CallbackVisitor(
						createNopAction(Span.class),
						errorCallback,
						createNopAction(BlockType.class),
						createNopAction(BlockType.class)
					);
				}
			},
			new Delegates.IAction2<CallbackVisitor, RazorError>() {
				@Override
				public void invoke(@Nullable final CallbackVisitor listener, @Nullable final RazorError expected) {
					assert listener != null;
					assert expected != null;
					listener.visitError(expected);
				}
			}
		);
	}

	@SuppressWarnings("unchecked")
	private static <T> void runSyncContextTest(final T defaultValue, final T expected, final Delegates.IFunc1<Delegates.IAction1<T>, CallbackVisitor> ctor, final Delegates.IAction2<CallbackVisitor, T> call) {
		final SynchronizationContext mockContext = mock(SynchronizationContext.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				final Object[] arguments = invocation.getArguments();
				assert arguments.length == 2;
				final SendOrPostCallback sendOrPostCallback = typeAs(arguments[0], SendOrPostCallback.class);
				sendOrPostCallback.invoke(expected);

				return null;
			}
		}).when(mockContext).post(any(SendOrPostCallback.class), anyObject());

		runCallbackTest(
			defaultValue,
			new Delegates.IFunc1<Delegates.IAction1<T>, CallbackVisitor>() {
				@Override
				public CallbackVisitor invoke(@Nullable final Delegates.IAction1<T> callback) {
					final CallbackVisitor listener = ctor.invoke(callback);
					assert listener != null;
					listener.setSynchronizationContext(mockContext);
					return listener;
				}
			},
			call,
			new Delegates.IAction2<T, T>() {
				@Override
				public void invoke(@Nullable final T original, @Nullable final T actual) {
					assertNotSame(original, actual);
					assertSame(expected, actual);
				}
			}
		);
	}

	private static void runOnStartBlockTest(final Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor> ctor) {
		runOnStartBlockTest(ctor, null);
	}

	private static void runOnStartBlockTest(final Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor> ctor, final Delegates.IAction2<BlockType, BlockType> verifyResults) {
		runCallbackTest(
			BlockType.Markup,
			ctor,
			new Delegates.IAction2<CallbackVisitor, BlockType>() {
				@Override
				public void invoke(@Nullable final CallbackVisitor listener, @Nullable final BlockType expected) {
					assert listener != null;
					assert expected != null;
					listener.visitStartBlock(new BlockBuilder().setType(expected).build());
				}
			},
			verifyResults
		);
	}

	private static void runOnEndBlockTest(final Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor> ctor) {
		runOnEndBlockTest(ctor, null);
	}

	private static void runOnEndBlockTest(final Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor> ctor, final Delegates.IAction2<BlockType, BlockType> verifyResults) {
		runCallbackTest(
			BlockType.Markup,
			ctor,
			new Delegates.IAction2<CallbackVisitor, BlockType>() {
				@Override
				public void invoke(@Nullable final CallbackVisitor listener, @Nullable final BlockType expected) {
					assert listener != null;
					assert expected != null;
					listener.visitEndBlock(new BlockBuilder().setType(expected).build());
				}
			},
			verifyResults
		);
	}

	private static void runOnErrorTest(final Delegates.IFunc1<Delegates.IAction1<RazorError>, CallbackVisitor> ctor) {
		runOnErrorTest(ctor, null);
	}

	private static void runOnErrorTest(final Delegates.IFunc1<Delegates.IAction1<RazorError>, CallbackVisitor> ctor, final Delegates.IAction2<RazorError, RazorError> verifyResults) {
		runCallbackTest(
			new RazorError("Foo", SourceLocation.Zero),
			ctor,
			new Delegates.IAction2<CallbackVisitor, RazorError>() {
				@Override
				public void invoke(@Nullable final CallbackVisitor listener, @Nullable final RazorError expected) {
					assert listener != null;
					assert expected != null;
					listener.visitError(expected);
				}
			},
			verifyResults
		);
	}

	private static void runOnEndSpanTest(final Delegates.IFunc1<Delegates.IAction1<Span>, CallbackVisitor> ctor) {
		runOnEndSpanTest(ctor, null);
	}

	private static void runOnEndSpanTest(final Delegates.IFunc1<Delegates.IAction1<Span>, CallbackVisitor> ctor, final Delegates.IAction2<Span, Span> verifyResults) {
		runCallbackTest(
			new SpanBuilder().build(),
			ctor,
			new Delegates.IAction2<CallbackVisitor, Span>() {
				@Override
				public void invoke(@Nullable final CallbackVisitor listener, @Nullable final Span expected) {
					assert listener != null;
					assert expected != null;
					listener.visitSpan(expected);
				}
			},
			verifyResults
		);
	}

	private static <T> void runCallbackTest(final T expected, final Delegates.IFunc1<Delegates.IAction1<T>, CallbackVisitor> ctor, final Delegates.IAction2<CallbackVisitor, T> call) {
		runCallbackTest(expected, ctor, call, null);
	}

	private static <T> void runCallbackTest(final T expected, final Delegates.IFunc1<Delegates.IAction1<T>, CallbackVisitor> ctor, final Delegates.IAction2<CallbackVisitor, T> call, final Delegates.IAction2<T, T> verifyResults) {
		final OutParam<T> actual = OutParam.of();
		final Delegates.IAction1<T> callback = new Delegates.IAction1<T>() {
			@Override
			public void invoke(@Nullable final T input) {
				actual.set(input);
			}
		};

		final CallbackVisitor listener = ctor.invoke(callback);

		call.invoke(listener, expected);

		if (verifyResults == null) {
			assertSame(expected, actual.value());
		}
		else {
			verifyResults.invoke(expected, actual.value());
		}
	}

	private static <T> Delegates.IAction1<T> createNopAction() {
		return new Delegates.IAction1<T>() {
			@Override
			public void invoke(@Nullable final T input) {
			}
		};
	}

	private static <T> Delegates.IAction1<T> createNopAction(final Class<T> ignored) {
		return new Delegates.IAction1<T>() {
			@Override
			public void invoke(@Nullable final T input) {
			}
		};
	}
}

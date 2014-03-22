/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
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
        runOnEndSpanTest(
                            callback -> {
                                assert callback != null;
                                return new CallbackVisitor(callback);
                            }
                        );
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
        runOnEndSpanTest(
                            input1 -> {
                                assert input1 != null;
                                return new CallbackVisitor(
                                                              input1,
                                                              createNopAction(RazorError.class)
                                );
                            }
                        );
    }

    @Test
    public void listenerConstructedWithSpanAndErrorCallbackCallsCallbackOnError() {
        runOnErrorTest(
                          input1 -> {
                              assert input1 != null;
                              return new CallbackVisitor(
                                                            createNopAction(Span.class),
                                                            input1
                              );
                          }
                      );
    }

    @Test
    public void listenerConstructedWithAllCallbacksCallsCallbackOnEndSpan() {
        runOnEndSpanTest(
                            input1 -> {
                                assert input1 != null;
                                return new CallbackVisitor(
                                                              input1,
                                                              createNopAction(RazorError.class),
                                                              createNopAction(BlockType.class),
                                                              createNopAction(BlockType.class)
                                );
                            }
                        );
    }

    @Test
    public void listenerConstructedWithAllCallbacksCallsCallbackOnError() {
        runOnErrorTest(
                          input1 -> {
                              assert input1 != null;
                              return new CallbackVisitor(
                                                            createNopAction(Span.class),
                                                            input1,
                                                            createNopAction(BlockType.class),
                                                            createNopAction(BlockType.class)
                              );
                          }
                      );
    }

    @Test
    public void listenerConstructedWithAllCallbacksCallsCallbackOnStartBlock() {
        runOnStartBlockTest(
                               input1 -> {
                                   assert input1 != null;
                                   return new CallbackVisitor(
                                                                 createNopAction(Span.class),
                                                                 createNopAction(RazorError.class),
                                                                 input1,
                                                                 createNopAction(BlockType.class)
                                   );
                               }
                           );
    }

    @Test
    public void listenerConstructedWithAllCallbacksCallsCallbackOnEndBlock() {
        runOnEndBlockTest(
                             input1 -> {
                                 assert input1 != null;
                                 return new CallbackVisitor(
                                                               createNopAction(Span.class),
                                                               createNopAction(RazorError.class),
                                                               createNopAction(BlockType.class),
                                                               input1
                                 );
                             }
                         );
    }

    @Test
    public void listenerCallsOnEndSpanCallbackUsingSynchronizationContextIfSpecified() {
        runSyncContextTest(
                              new SpanBuilder().build(),
                              new SpanBuilder().build(),
                              spanCallback -> {
                                  assert spanCallback != null;
                                  return new CallbackVisitor(
                                                                spanCallback,
                                                                createNopAction(RazorError.class),
                                                                createNopAction(BlockType.class),
                                                                createNopAction(BlockType.class)
                                  );
                              },
                              (listener, expected) -> {
                                  assert listener != null;
                                  assert expected != null;
                                  listener.visitSpan(expected);
                              }
                          );
    }

    @Test
    public void listenerCallsOnStartBlockCallbackUsingSynchronizationContextIfSpecified() {
        runSyncContextTest(
                              BlockType.values()[0],
                              BlockType.Template,
                              startBlockCall -> {
                                  assert startBlockCall != null;
                                  return new CallbackVisitor(
                                                                createNopAction(Span.class),
                                                                createNopAction(RazorError.class),
                                                                startBlockCall,
                                                                createNopAction(BlockType.class)
                                  );
                              },
                              (listener, expected) -> {
                                  assert listener != null;
                                  listener.visitStartBlock(new BlockBuilder().setType(expected).build());
                              }
                          );
    }

    @Test
    public void listenerCallsOnEndBlockCallbackUsingSynchronizationContextIfSpecified() {
        runSyncContextTest(
                              BlockType.values()[0],
                              BlockType.Template,
                              endBlockCallback -> {
                                  assert endBlockCallback != null;
                                  return new CallbackVisitor(
                                                                createNopAction(Span.class),
                                                                createNopAction(RazorError.class),
                                                                createNopAction(BlockType.class),
                                                                endBlockCallback
                                  );
                              },
                              (listener, expected) -> {
                                  assert listener != null;
                                  listener.visitEndBlock(new BlockBuilder().setType(expected).build());
                              }
                          );
    }

    @Test
    public void listenerCallsOnErrorCallbackUsingSynchronizationContextIfSpecified() {
        runSyncContextTest(
                              new RazorError("Foo", SourceLocation.Zero),
                              new RazorError("Bar", 42, 42, 42),
                              errorCallback -> {
                                  assert errorCallback != null;
                                  return new CallbackVisitor(
                                                                createNopAction(Span.class),
                                                                errorCallback,
                                                                createNopAction(BlockType.class),
                                                                createNopAction(BlockType.class)
                                  );
                              },
                              (listener, expected) -> {
                                  assert listener != null;
                                  assert expected != null;
                                  listener.visitError(expected);
                              }
                          );
    }

    @SuppressWarnings("unchecked")
    private static <T> void runSyncContextTest(final T defaultValue, final T expected,
                                               final Delegates.IFunc1<Delegates.IAction1<T>, CallbackVisitor> ctor,
                                               final Delegates.IAction2<CallbackVisitor, T> call
                                              ) {
        final SynchronizationContext mockContext = mock(SynchronizationContext.class);
        doAnswer(
                    invocation -> {
                        final Object[] arguments = invocation.getArguments();
                        assert arguments.length == 2;
                        final SendOrPostCallback sendOrPostCallback = typeAs(arguments[0], SendOrPostCallback.class);
                        sendOrPostCallback.invoke(expected);

                        return null;
                    }
                ).when(mockContext).post(any(SendOrPostCallback.class), anyObject());

        runCallbackTest(
                           defaultValue,
                           callback -> {
                               final CallbackVisitor listener = ctor.invoke(callback);
                               assert listener != null;
                               listener.setSynchronizationContext(mockContext);
                               return listener;
                           },
                           call,
                           (original, actual) -> {
                               assertNotSame(original, actual);
                               assertSame(expected, actual);
                           }
                       );
    }

    private static void runOnStartBlockTest(final Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor> ctor) {
        runOnStartBlockTest(ctor, null);
    }

    private static void runOnStartBlockTest(final Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor> ctor,
                                            final Delegates.IAction2<BlockType, BlockType> verifyResults
                                           ) {
        runCallbackTest(
                           BlockType.Markup,
                           ctor,
                           (listener, expected) -> {
                               assert listener != null;
                               assert expected != null;
                               listener.visitStartBlock(new BlockBuilder().setType(expected).build());
                           },
                           verifyResults
                       );
    }

    private static void runOnEndBlockTest(final Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor> ctor) {
        runOnEndBlockTest(ctor, null);
    }

    private static void runOnEndBlockTest(final Delegates.IFunc1<Delegates.IAction1<BlockType>, CallbackVisitor> ctor,
                                          final Delegates.IAction2<BlockType, BlockType> verifyResults
                                         ) {
        runCallbackTest(
                           BlockType.Markup,
                           ctor,
                           (listener, expected) -> {
                               assert listener != null;
                               assert expected != null;
                               listener.visitEndBlock(new BlockBuilder().setType(expected).build());
                           },
                           verifyResults
                       );
    }

    private static void runOnErrorTest(final Delegates.IFunc1<Delegates.IAction1<RazorError>, CallbackVisitor> ctor) {
        runOnErrorTest(ctor, null);
    }

    private static void runOnErrorTest(final Delegates.IFunc1<Delegates.IAction1<RazorError>, CallbackVisitor> ctor,
                                       final Delegates.IAction2<RazorError, RazorError> verifyResults
                                      ) {
        runCallbackTest(
                           new RazorError("Foo", SourceLocation.Zero),
                           ctor,
                           (listener, expected) -> {
                               assert listener != null;
                               assert expected != null;
                               listener.visitError(expected);
                           },
                           verifyResults
                       );
    }

    private static void runOnEndSpanTest(final Delegates.IFunc1<Delegates.IAction1<Span>, CallbackVisitor> ctor) {
        runOnEndSpanTest(ctor, null);
    }

    private static void runOnEndSpanTest(final Delegates.IFunc1<Delegates.IAction1<Span>, CallbackVisitor> ctor,
                                         final Delegates.IAction2<Span, Span> verifyResults
                                        ) {
        runCallbackTest(
                           new SpanBuilder().build(),
                           ctor,
                           (listener, expected) -> {
                               assert listener != null;
                               assert expected != null;
                               listener.visitSpan(expected);
                           },
                           verifyResults
                       );
    }

    private static <T> void runCallbackTest(final T expected,
                                            final Delegates.IFunc1<Delegates.IAction1<T>, CallbackVisitor> ctor,
                                            final Delegates.IAction2<CallbackVisitor, T> call
                                           ) {
        runCallbackTest(expected, ctor, call, null);
    }

    private static <T> void runCallbackTest(final T expected,
                                            final Delegates.IFunc1<Delegates.IAction1<T>, CallbackVisitor> ctor,
                                            final Delegates.IAction2<CallbackVisitor, T> call,
                                            final Delegates.IAction2<T, T> verifyResults
                                           ) {
        final OutParam<T> actual = OutParam.of();
        final Delegates.IAction1<T> callback = actual::set;

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
        return input -> {
        };
    }

    private static <T> Delegates.IAction1<T> createNopAction(final Class<T> ignored) {
        return input -> {
        };
    }
}

/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;

import org.mockito.exceptions.Reporter;
import org.mockito.internal.MockHandler;
import org.mockito.internal.MockUtil;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.OngoingStubbing;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.progress.VerificationMode;
import org.mockito.internal.stubbing.VoidMethodStubbable;

/**
 * Enables mock objects creation, verification and stubbing.
 * 
 * <h3>Let's verify!</h3>
 * 
 * <pre>
 * //Let's import Mockito statically so that code looks clearer
 * import static org.mockito.Mockito.*;
 * 
 * //mock creation
 * List mockedList = mock(List.class);
 * 
 * //using mock object
 * mockedList.add("one");
 * mockedList.clear();
 * 
 * //verification
 * verify(mockedList).add("one");
 * verify(mockedList).clear();
 * </pre>
 * 
 * <p>
 * Once created, mock object will record all invocations. Then you can
 * selectively verify whatever interaction you are interested in.
 * 
 * <h3>How about some stubbing?</h3>
 * 
 * <pre>
 * //You can create mocks of concrete classes, not only interfaces
 * LinkedList mockedList = mock(LinkedList.class);
 * 
 * //stubbing
 * stub(mockedList.get(0)).andReturn("first");
 * stub(mockedList.get(1)).andReturn("second");
 * stub(mockedList.get(2)).andThrow(new RuntimeException());
 * 
 * //following prints "first"
 * System.out.println(mockedList.get(0));
 * 
 * //following prints "second" three times
 * System.out.println(mockedList.get(1));
 * System.out.println(mockedList.get(1));
 * System.out.println(mockedList.get(1));
 * 
 * //following throws runtime exception
 * System.out.println(mockedList.get(2));
 * 
 * //following prints "null"
 * System.out.println(mockedList.get(999));
 * 
 * //if you want you can still verify stubbed invocation
 * verify(mockedList.get(0));
 * </pre>
 * 
 * <p>
 * Once stubbed, mock object will always return stubbed value regardless of how many times it is called. 
 * <p>
 * By default, for all methods that return value mock object will return null,
 * appropriate primitive value (0, false, etc.) or empty collection. 
 * 
 * <h3>Argument matchers</h3>
 * 
 * <pre>
 *  //stubbing using anyInt() argument matcher
 *  stub(mockedList.get(anyInt())).andReturn("element");
 *  
 *  //following prints "element"
 *  System.out.println(mockedList.get(999));
 *  
 *  //you can also verify using argument matcher
 *  verify(mockedList).get(anyInt());
 * </pre>
 * 
 * <p>
 * Argument matchers allow less constrained verification or stubbing. Link to argument matchers documentation needed.
 * 
 * <h3>Exact number of invocations verification</h3>
 *
 *<pre>
 *  //using mock 
 *  mockedList.add("once");
 *  
 *  mockedList.add("twice");
 *  mockedList.add("twice");
 *  
 *  mockedList.add("three times");
 *  mockedList.add("three times");
 *  mockedList.add("three times");
 *  
 *  //following two verifications work exactly the same
 *  verify(mockedList, times(1)).add("once");
 *  verify(mockedList).add("once");
 *  
 *  //exact number of invocation verification
 *  verify(mockedList, times(2)).add("twice");
 *  verify(mockedList, times(3)).add("three times");
 *  
 *  //verification using atLeastOnce()
 *  verify(mockedList, atLeastOnce()).add("three times");
 * </pre>
 * 
 * <p>
 * If times(x) is not given times(1) is assumed. Therefore using times(1) explicitly should be omitted.
 * 
 * <h3>Stubbing void methods with exceptions</h3>
 * 
 * <pre>
 *   stubVoid(mockedList).toThrow(new RuntimeException()).on().clear();
 *   
 *   //following throws exception
 *   mockedList.clear();
 * </pre>
 *
 * <h3>Finding redundant invocations</h3>
 * 
 * <pre>
 *   //using mocks
 *   mockedList.add("one");
 *   mockedList.add("two");
 *   
 *   verify(mockedList).add("one");
 *   
 *   //following verification will fail 
 *   verifyNoMoreInteractions(mockedList);
 * </pre>
 * 
 * See more {@link Mockito.verifyNoMoreInteractions}
 * 
 * <h3>Strict order verification</h3>
 * 
 * <pre>
 *   List mockOne = mock(List.class);
 *   List mockTwo = mock(List.class);
 *   
 *   //using mocks
 *   mockOne.add("one");
 *   mockTwo.add("two");
 *   
 *   //create strict verifier 
 *   Strictly strictly = createStrictOrderVerifier(mockOne, mockTwo);
 *   
 *   //following will make sure that mockOne was called before mockTwo
 *   strictly.verify(mockOne).add("one");
 *   strictly.verify(mockTwo).add("two");
 * </pre>
 * 
 * <p>
 * Strict verification is required only in some cases and in most cases ordinary verification is enough. 
 * 
 * <h3>Making sure no interactions happened on mock</h3>
 * 
 * <pre>
 *   //using mocks - only mockOne is interacted
 *   mockOne.add("one");
 *   
 *   //ordinary verification
 *   verify(mockOne).add("one");
 *   
 *   //other mocks should not be interacted
 *   verifyZeroInteractions(mockTwo, mockThree);
 *   
 *   //following works exactly the same as above
 *   verifyNoMoreInteractions(mockTwo, mockThree);
 * </pre>
 * 
 * <p>
 * Instead of verifyZeroInteractions() you can call verifyNoMoreInteractions() but 
 * the first one is more explicit and can read better.
 * 
 */
public class Mockito extends Matchers {

    private static final Reporter REPORTER = new Reporter();
    static final MockingProgress MOCKING_PROGRESS = new ThreadSafeMockingProgress();

    /**
     * Creates mock object of given class or interface.
     * <p>
     * 
     * See examples {@link Mockito}
     * 
     * @param classToMock
     * @return mock object
     */
    public static <T> T mock(Class<T> classToMock) {
        return MockUtil.createMock(classToMock, MOCKING_PROGRESS);
    }

    /**
     * Stubs with return value or exception, e.g:
     * <pre>
     *   stub(mock.countElements()).andReturn(10);
     *   
     *   stub(mock.countElements()).andThrow(new RuntimeException());
     * </pre>
     * 
     * See examples {@link Mockito}
     * 
     * @param methodCallToStub
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> OngoingStubbing<T> stub(T methodCallToStub) {
        MOCKING_PROGRESS.stubbingStarted();

        OngoingStubbing stubbable = MOCKING_PROGRESS.pullStubbable();
        if (stubbable == null) {
            REPORTER.missingMethodInvocation();
        }
        return stubbable;
    }

    /**
     * Verifies certain behavior, e.g:
     * <pre>
     *   verify(mock).someMethod("some arg");
     * </pre>
     * 
     * See examples {@link Mockito}
     * 
     * @param mock
     * @return
     */
    public static <T> T verify(T mock) {
        return verify(mock, times(1));
    }

    /**
     * Verifies certain behavior happened exact number of times, e.g:
     * <pre>
     *   verify(mock, times(5)).someMethod("one");
     *   
     *   verify(mock, atLeastOnce()).someMethod("two");
     * </pre>
     * 
     * See examples {@link Mockito}
     * 
     * @param mock
     * @param mode
     * @return
     */
    public static <T> T verify(T mock, VerificationMode mode) {
        MockUtil.validateMock(mock);
        MOCKING_PROGRESS.verificationStarted(mode);
        return mock;
    }

    /**
     * Throws an AssertionError if any of given mocks has any unverified
     * interaction.
     * <p>
     * Use this method after you verified your mocks - to make sure that nothing
     * else was invoked on your mocks.
     * <p>
     * It's a good pattern not to use this method in every test method. Test
     * methods should focus on different behavior/interaction and it's not
     * necessary to call verifyNoMoreInteractions() all the time
     * <p>
     * Stubbed invocations are also treated as interactions.
     * <p>
     * Example:
     * 
     * <pre>
     * //interactions
     * mock.doSomething();
     * mock.doSomethingUnexpected();
     * 
     * //verification
     * verify(mock).doSomething();
     * verifyNoMoreInteractions(mock);
     * 
     * //oups: 'doSomethingUnexpected()' is unexpected
     * </pre>
     * 
     * See examples {@link Mockito}
     * 
     * @param mocks to be verified
     */
    public static void verifyNoMoreInteractions(Object... mocks) {
        assertMocksNotEmpty(mocks);
        MOCKING_PROGRESS.validateState();
        for (Object mock : mocks) {
            MockUtil.getMockHandler(mock).verifyNoMoreInteractions();
        }
    }

    /**
     * Verifies that no interactions happened on given mocks.
     * Instead of verifyZeroInteractions() you can call verifyNoMoreInteractions() but 
     * the first one is more explicit and can read better.
     * <p>
     * 
     * See examples {@link Mockito}
     * 
     * @param mocks to be verified
     */
    public static void verifyZeroInteractions(Object... mocks) {
        verifyNoMoreInteractions(mocks);
    }

    private static void assertMocksNotEmpty(Object[] mocks) {
        if (mocks.length == 0) {
            REPORTER.mocksHaveToBePassedAsArguments();
        }
    }

    /**
     * Stubs void method with exception, e.g:
     * <pre>
     *   stubVoid(mock).toThrow(new RuntimeException()).on().someMethod("some arg");
     * </pre>
     * 
     * See examples {@link Mockito}
     * 
     * @param mock to stub
     * @return
     */
    public static <T> VoidMethodStubbable<T> stubVoid(T mock) {
        MockHandler<T> handler = MockUtil.getMockHandler(mock);
        MOCKING_PROGRESS.stubbingStarted();
        return handler;
    }

    /**
     * Creates strict verifier that allows verifying mocks in order.
     * <p>
     * See examples {@link Mockito}
     * 
     * @param mocks to verified in strict order
     * @return
     */
    public static Strictly createStrictOrderVerifier(Object... mocks) {
        if (mocks.length == 0) {
            REPORTER.mocksHaveToBePassedWhenCreatingStrictly();
        }
        StrictOrderVerifier strictOrderVerifier = new StrictOrderVerifier();
        for (Object mock : mocks) {
            MockUtil.validateMock(mock);
            strictOrderVerifier.addMockToBeVerifiedStrictly(mock);
        }
        return strictOrderVerifier;
    }

    /**
     * Allows at-least-once verification, e.g:
     * <pre>
     *   verify(mock, atLeastOnce()).someMethod("some arg");
     * </pre>
     * 
     * See examples {@link Mockito}
     * 
     * @return
     */
    public static VerificationMode atLeastOnce() {
        return VerificationMode.atLeastOnce();
    }

    /**
     * Allows exact number of invocations verification, e.g:
     * <pre>
     *   verify(mock, times(2)).someMethod("some arg");
     * </pre>
     * 
     * See examples {@link Mockito}
     * 
     * @param wantedNumberOfInvocations wanted number of invocations 
     * @return
     */
    public static VerificationMode times(int wantedNumberOfInvocations) {
        return VerificationMode.times(wantedNumberOfInvocations);
    }
}
/*
 * Copyright (c) 2007 Mockito contributors 
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification;

import java.util.List;

import org.mockito.exceptions.Reporter;
import org.mockito.internal.invocation.Invocation;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.invocation.InvocationsCalculator;
import org.mockito.internal.progress.VerificationMode;

public class NoMoreInvocationsVerifier implements Verifier {

    private final Reporter reporter = new Reporter();
    private final InvocationsCalculator calculator = new InvocationsCalculator();
    
    public void verify(List<Invocation> invocations, InvocationMatcher wanted, VerificationMode mode) {
        if (mode.isExplicit()) {
            return;
        }

        Invocation unverified = calculator.getFirstUnverified(invocations);
        if (unverified != null) {
            reporter.noMoreInteractionsWanted(unverified.toString(), unverified.getStackTrace());
        }
    }
}
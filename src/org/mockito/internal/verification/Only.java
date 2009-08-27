package org.mockito.internal.verification;

import java.util.List;

import org.mockito.exceptions.Reporter;
import org.mockito.internal.invocation.Invocation;
import org.mockito.internal.invocation.InvocationMarker;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.invocation.InvocationsFinder;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.internal.verification.api.VerificationMode;

public class Only implements VerificationMode {

	private final InvocationsFinder finder = new InvocationsFinder();
	private final InvocationMarker marker = new InvocationMarker();
	private final Reporter reporter = new Reporter();

	@Override
	public void verify(VerificationData data) {
		InvocationMatcher wantedMatcher = data.getWanted();
		List<Invocation> invocations = data.getAllInvocations();
		List<Invocation> chunk = finder.findInvocations(invocations,wantedMatcher);
		if (invocations.size() != 1 && chunk.size() > 0) {
			marker.markVerified(chunk.get(0), wantedMatcher);
			Invocation unverified = finder.findFirstUnverified(invocations);
			reporter.noMoreInteractionsWanted(unverified);
		} else if (invocations.size() != 1 || chunk.size() == 0) {
			reporter.wantedButNotInvoked(wantedMatcher);
		}
	}

}

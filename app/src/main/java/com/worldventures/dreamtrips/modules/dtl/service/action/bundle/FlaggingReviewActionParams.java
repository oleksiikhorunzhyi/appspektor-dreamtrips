package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import org.immutables.value.Value;

@Value.Immutable
public interface FlaggingReviewActionParams extends HttpActionParams {

   String authorIpAddress();

   Integer contentType();

   Integer feedbackType();
}

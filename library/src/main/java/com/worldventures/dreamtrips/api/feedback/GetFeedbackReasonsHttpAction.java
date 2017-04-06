package com.worldventures.dreamtrips.api.feedback;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.FeedbackReason;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/feedbacks/reasons")
public class GetFeedbackReasonsHttpAction extends AuthorizedHttpAction {

    @Response
    List<FeedbackReason> reasons;

    public List<FeedbackReason> reasons() {
        return reasons;
    }
}

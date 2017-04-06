package com.worldventures.dreamtrips.api.feedback;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;

@HttpAction(value = "/api/feedbacks", method = HttpAction.Method.POST)
public class SendFeedbackHttpAction extends AuthorizedHttpAction {
    @Body
    public final Feedback feedback;

    public SendFeedbackHttpAction(Feedback feedback) {
        this.feedback = feedback;
    }
}

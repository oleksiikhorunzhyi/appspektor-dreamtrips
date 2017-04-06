package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.feedback.GetFeedbackReasonsHttpAction;
import com.worldventures.dreamtrips.api.feedback.SendFeedbackHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;
import com.worldventures.dreamtrips.api.feedback.model.FeedbackAttachment;
import com.worldventures.dreamtrips.api.feedback.model.FeedbackReason;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableFeedback;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Feedback")
public class FeedbackTest extends BaseTestWithSession {

    private List<FeedbackReason> feedbackReasons;

    @Fixture("feedback_general_metadata")
    Feedback.Metadata feedbackMetadata;
    @Fixture("feedback_smartcard_metadata")
    Feedback.SmartCardMetadata smartcardMetadata;
    @Fixture("feedback_attachment")
    FeedbackAttachment attachment;

    @Test
    public void testGetFeedbackReasons() {
        GetFeedbackReasonsHttpAction action = execute(new GetFeedbackReasonsHttpAction());
        assertThat(action.reasons()).isNotNull().isNotEmpty();

        this.feedbackReasons = action.reasons();
    }

    @Test(dependsOnMethods = {"testGetFeedbackReasons"})
    public void testSendFeedbackWithoutSmartCardInfo() {
        FeedbackReason feedbackReason = this.feedbackReasons.get(0);
        Feedback feedback = feedbackBuilder(feedbackReason).build();

        sendAndCheck(feedback);
    }

    @Test(dependsOnMethods = {"testGetFeedbackReasons"})
    public void testSendFeedbackWithSmartcardInfo() {
        FeedbackReason feedbackReason = this.feedbackReasons.get(0); // todo use proper category
        Feedback feedback = feedbackBuilder(feedbackReason)
                .smartCardMetadata(smartcardMetadata)
                .build();

        sendAndCheck(feedback);
    }

    @Test(dependsOnMethods = {"testGetFeedbackReasons"})
    public void testSendFeedbackWithAttachments() {
        FeedbackReason feedbackReason = this.feedbackReasons.get(0);
        Feedback feedback = feedbackBuilder(feedbackReason)
                .addAllAttachments(Collections.singletonList(attachment))
                .build();

        sendAndCheck(feedback);
    }

    private ImmutableFeedback.Builder feedbackBuilder(FeedbackReason feedbackReason) {
        return ImmutableFeedback.builder()
                .reasonId(feedbackReason.id())
                .text("test")
                .metadata(feedbackMetadata);
    }

    private void sendAndCheck(Feedback feedback) {
        SendFeedbackHttpAction action = execute(new SendFeedbackHttpAction(feedback));
        assertThat(action.statusCode()).isEqualTo(204);
    }

}

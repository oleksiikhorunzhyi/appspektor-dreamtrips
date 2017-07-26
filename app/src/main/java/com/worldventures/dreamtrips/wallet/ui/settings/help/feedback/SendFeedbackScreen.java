package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen;

import rx.Observable;

public interface SendFeedbackScreen extends BaseFeedbackScreen {

   void applyFeedbackType(FeedbackType feedbackType);

   Observable<CharSequence> getTextFeedbackMessage();

   FeedbackType getFeedbackType();
}

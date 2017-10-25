package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter;

public interface SendFeedbackPresenter extends BaseSendFeedbackPresenter<SendFeedbackScreen> {
   void sendFeedback(String text);
}

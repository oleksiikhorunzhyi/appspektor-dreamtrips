package com.worldventures.wallet.ui.settings.help.feedback;

import com.worldventures.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter;

public interface SendFeedbackPresenter extends BaseSendFeedbackPresenter<SendFeedbackScreen> {
   void sendFeedback(String text);
}

package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base;

import android.content.Context;

import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;


public interface BaseFeedbackScreen extends WalletScreen {

   void changeActionSendMenuItemEnabled(boolean enable);

   void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder);

   void changeAddPhotosButtonEnabled(boolean enable);

   void setAttachments(List<EntityStateHolder<FeedbackImageAttachment>> attachments);

   void updateAttachment(EntityStateHolder<FeedbackImageAttachment> updatedHolder);

   void pickPhoto();

   OperationView<SendWalletFeedbackCommand> provideOperationSendFeedback();

   Context getViewContext();
}

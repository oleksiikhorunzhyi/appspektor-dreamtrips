package com.worldventures.wallet.ui.settings.help.feedback.base;

import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.wallet.ui.common.base.WalletPresenter;

import java.util.List;

public interface BaseSendFeedbackPresenter<S extends BaseFeedbackScreen> extends WalletPresenter<S> {

   void handleAttachedImages(List<PhotoPickerModel> chosenImages);

   int getAttachmentsCount();

   void goBack();

   void clearAttachments();

   void fetchAttachments();

   void onRemoveAttachment(EntityStateHolder<FeedbackImageAttachment> holder);

   void chosenAttachments();

   void onRetryUploadingAttachment(EntityStateHolder<FeedbackImageAttachment> attachmentHolder);

   void openFullScreenPhoto(EntityStateHolder<FeedbackImageAttachment> holder);

}

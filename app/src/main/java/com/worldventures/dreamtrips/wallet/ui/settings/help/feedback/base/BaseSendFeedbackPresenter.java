package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base;

import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.social.ui.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

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

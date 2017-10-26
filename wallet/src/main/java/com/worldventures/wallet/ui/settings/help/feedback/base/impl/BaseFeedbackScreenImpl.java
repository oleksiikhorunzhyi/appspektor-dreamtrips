package com.worldventures.wallet.ui.settings.help.feedback.base.impl;


import android.content.Context;
import android.os.Bundle;

import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen;
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter;

public abstract class BaseFeedbackScreenImpl<S extends BaseFeedbackScreen, P extends BaseSendFeedbackPresenter> extends WalletBaseController<S, P> implements BaseFeedbackScreen {

   public BaseFeedbackScreenImpl() {
      super();
   }

   public BaseFeedbackScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   public Context getViewContext() {
      return getContext();
   }

   @Override
   public void pickPhoto() {
      final MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(pickerResultAttachment -> {
         if (!pickerResultAttachment.isEmpty()) {
            getPresenter().handleAttachedImages(pickerResultAttachment.getChosenImages());
         }
      });
      mediaPickerDialog.show(BaseFeedbackPresenterImpl.MAX_PHOTOS_ATTACHMENT - getPresenter().getAttachmentsCount());
   }
}

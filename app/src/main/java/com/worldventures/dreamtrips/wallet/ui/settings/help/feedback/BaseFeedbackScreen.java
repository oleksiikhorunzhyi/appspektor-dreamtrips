package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialog;

import static com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.BaseSendFeedbackPresenter.MAX_PHOTOS_ATTACHMENT;


public abstract class BaseFeedbackScreen<S extends BaseSendFeedbackPresenter.Screen, P extends BaseSendFeedbackPresenter<S>, T extends StyledPath> extends WalletLinearLayout<S, P, T> implements BaseSendFeedbackPresenter.Screen {

   public BaseFeedbackScreen(Context context) {
      super(context);
   }

   public BaseFeedbackScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public void pickPhoto() {
      final MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(pickerResultAttachment -> {
         if (!pickerResultAttachment.isEmpty()) {
            getPresenter().handleAttachedImages(pickerResultAttachment.getChosenImages());
         }
      });
      mediaPickerDialog.show(MAX_PHOTOS_ATTACHMENT - getPresenter().getAttachmentsCount());
   }
}

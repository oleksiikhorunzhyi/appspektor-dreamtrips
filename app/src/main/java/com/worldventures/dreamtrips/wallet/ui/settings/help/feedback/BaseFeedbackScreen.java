package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.dialog.WalletPickerDialog;

import static com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.BaseSendFeedbackPresenter.MAX_PHOTOS_ATTACHMENT;


public abstract class BaseFeedbackScreen<S extends BaseSendFeedbackPresenter.Screen, P extends BaseSendFeedbackPresenter<S>, T extends StyledPath> extends WalletLinearLayout<S, P, T> implements BaseSendFeedbackPresenter.Screen {

   private WalletPickerDialog walletPickerDialog;

   public BaseFeedbackScreen(Context context) {
      super(context);
   }

   public BaseFeedbackScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public void pickPhoto() {
      walletPickerDialog = new WalletPickerDialog(getContext(), getInjector());
      walletPickerDialog.setOnDoneListener(pickerResultAttachment -> {
         if (!pickerResultAttachment.isEmpty()) {
            getPresenter().handleAttachedImages(pickerResultAttachment);
         }
      });
      walletPickerDialog.show(true, MAX_PHOTOS_ATTACHMENT - getPresenter().getAttachmentsCount());
   }
}

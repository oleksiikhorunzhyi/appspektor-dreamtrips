package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.impl;


import android.content.Context;
import android.os.Bundle;

import com.worldventures.core.modules.picker.helper.PickerPermissionChecker;
import com.worldventures.core.modules.picker.helper.PickerPermissionUiHandler;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter;

import javax.inject.Inject;

public abstract class BaseFeedbackScreenImpl<S extends BaseFeedbackScreen, P extends BaseSendFeedbackPresenter> extends WalletBaseController<S, P> implements BaseFeedbackScreen {

   @Inject PickerPermissionUiHandler pickerPermissionUiHandler;
   @Inject PermissionUtils permissionUtils;

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
   public void showPermissionDenied(String[] permissions) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionUiHandler.showPermissionDenied(getView());
      }
   }

   @Override
   public void showPermissionExplanationText(String[] permissions) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionUiHandler.showRational(getContext(), answer -> getPresenter().recheckPermission(permissions, answer));
      }
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

package com.worldventures.dreamtrips.wallet.ui.common.picker.dialog;


import android.view.KeyEvent;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerAttachment;

import java.util.Collections;
import java.util.List;

import icepick.State;

public class WalletPickerDialogPresenterImpl extends MvpBasePresenter<WalletPickerDialogView> implements WalletPickerDialogPresenter {

   @State WalletPickerAttachment pickerAttachment;

   public WalletPickerDialogPresenterImpl() {
   }

   @Override
   public void attachView(WalletPickerDialogView view) {
      super.attachView(view);
      observeAttachedPhotos();
   }

   public void observeAttachedPhotos() {
      getView()
            .attachedPhotos()
            .compose(getView().lifecycle())
            .subscribe(attachment -> {
               this.pickerAttachment = attachment;
               if (getView().getPickLimit() > 1) {
                  getView().updatePickedItemsCount(pickerAttachment.getChosenPhotos().size());
               }
               if (pickerAttachment.getPickerSource() == WalletPickerAttachment.WalletPickerSource.CAMERA) {
                  getView().onDone();
               }
            });
   }

   @Override
   public void detachView(boolean retainInstance) {
      super.detachView(retainInstance);
      performCleanUp();
   }

   @Override
   public List<BasePickerViewModel> providePickerResult() {
      return pickerAttachment != null ? pickerAttachment.getChosenPhotos() : Collections.emptyList();
   }

   @Override
   public boolean handleKeyPress(int keyCode, KeyEvent keyEvent) {
      boolean shouldConsume = false;
      if (keyCode == KeyEvent.KEYCODE_BACK
            && keyEvent.getAction() == KeyEvent.ACTION_DOWN
            && getView().getCurrentStep() != WalletPickerStep.GALLERY) {
         getView().goBack();
         shouldConsume = true;
      }
      return shouldConsume;
   }

   @Override
   public void performCleanUp() {
      pickerAttachment = null;
   }
}

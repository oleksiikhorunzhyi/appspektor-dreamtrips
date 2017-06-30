package com.worldventures.dreamtrips.wallet.ui.common.picker.dialog;


import android.view.KeyEvent;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModelImpl;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryVideoModel;

import java.util.ArrayList;

import icepick.State;

public class WalletPickerDialogPresenterImpl extends MvpBasePresenter<WalletPickerDialogView> implements WalletPickerDialogPresenter {

   @State ArrayList<BasePickerViewModel> pickerAttachment;
   @State MediaPickerAttachment attachment;

   public WalletPickerDialogPresenterImpl() {
   }

   @Override
   public void attachView(WalletPickerDialogView view) {
      super.attachView(view);
      pickerAttachment = new ArrayList<>();
      attachment = new MediaPickerAttachment(getView().getRequestId());
      observeAttachedPhotos();
   }

   public void observeAttachedPhotos() {
      getView()
            .attachedMedia()
            .compose(getView().lifecycle())
            .subscribe(attachment -> {
               pickerAttachment.clear();
               pickerAttachment.addAll(attachment);
               if (getView().getPickLimit() > 1) {
                  getView().updatePickedItemsCount(pickerAttachment.size());
               }
               if (getView().getPickLimit() == 1 && pickerAttachment.get(0).getSource() == MediaAttachment.Source.CAMERA) {
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
   public MediaPickerAttachment providePickerResult() {
      for (BasePickerViewModel model : pickerAttachment) {
         MediaPickerModelImpl mediaPickerModel = null;
         if (model.getType() == MediaPickerModel.Type.VIDEO) {
            mediaPickerModel = new VideoPickerModel(model.getAbsolutePath(), ((WalletGalleryVideoModel) model).getDuration());
         } else if (model.getType() == MediaPickerModel.Type.PHOTO){
            mediaPickerModel = new PhotoPickerModel(model.getAbsolutePath(), model.getDateTaken());
         }
         attachment.addMedia(mediaPickerModel);
      }
      return attachment;
   }

   @Override
   public boolean handleKeyPress(int keyCode, KeyEvent keyEvent) {
      boolean shouldConsume = false;
      if (keyCode == KeyEvent.KEYCODE_BACK
            && keyEvent.getAction() == KeyEvent.ACTION_DOWN
            && getView().canGoBack()) {
         getView().goBack();
         shouldConsume = true;
      }
      return shouldConsume;
   }

   @Override
   public void performCleanUp() {
      pickerAttachment = null;
      attachment = null;
   }
}

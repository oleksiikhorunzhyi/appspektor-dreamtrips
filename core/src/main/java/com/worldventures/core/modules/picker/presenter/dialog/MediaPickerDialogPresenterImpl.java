package com.worldventures.core.modules.picker.presenter.dialog;


import android.view.KeyEvent;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.MediaPickerModel;
import com.worldventures.core.modules.picker.model.MediaPickerModelImpl;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.model.VideoPickerModel;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialogView;
import com.worldventures.core.modules.picker.viewmodel.BaseMediaPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryVideoPickerViewModel;

import java.util.ArrayList;

import icepick.State;

public class MediaPickerDialogPresenterImpl extends MvpBasePresenter<MediaPickerDialogView> implements MediaPickerDialogPresenter {

   @State ArrayList<BaseMediaPickerViewModel> pickerAttachment;

   @Override
   public void attachView(MediaPickerDialogView view) {
      super.attachView(view);
      pickerAttachment = new ArrayList<>();
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
               if (checkCameraAttachments()) {
                  getView().onDone();
               }
            });
   }

   private boolean checkCameraAttachments() {
      for (BaseMediaPickerViewModel model : pickerAttachment) {
         if (model.getSource() == MediaPickerAttachment.Source.CAMERA) {
            return true;
         }
      }
      return false;
   }

   @Override
   public void detachView(boolean retainInstance) {
      super.detachView(retainInstance);
      performCleanUp();
   }

   @Override
   public MediaPickerAttachment providePickerResult() {
      final MediaPickerAttachment attachment = new MediaPickerAttachment(getView().getRequestId());
      for (BaseMediaPickerViewModel model : pickerAttachment) {
         MediaPickerModelImpl mediaPickerModel = null;
         if (model.getType() == MediaPickerModel.Type.VIDEO) {
            mediaPickerModel = new VideoPickerModel(model.getAbsolutePath(), ((GalleryVideoPickerViewModel) model).getDuration());
         } else if (model.getType() == MediaPickerModel.Type.PHOTO) {
            mediaPickerModel = new PhotoPickerModel(model.getAbsolutePath(), model.getDateTaken());
         }
         mediaPickerModel.setSource(model.getSource());
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

   private void performCleanUp() {
      pickerAttachment = null;
   }
}

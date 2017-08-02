package com.worldventures.dreamtrips.modules.picker.presenter.dialog;


import android.view.KeyEvent;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModelImpl;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.picker.model.BaseMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryVideoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialogView;

import java.util.ArrayList;

import icepick.State;

public class MediaPickerDialogPresenterImpl extends MvpBasePresenter<MediaPickerDialogView> implements MediaPickerDialogPresenter {

   @State ArrayList<BaseMediaPickerViewModel> pickerAttachment;
   @State MediaPickerAttachment attachment;

   public MediaPickerDialogPresenterImpl() {
   }

   @Override
   public void attachView(MediaPickerDialogView view) {
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
               if (checkCameraAttachments()) {
                  getView().onDone();
               }
            });
   }

   private boolean checkCameraAttachments() {
      for (BaseMediaPickerViewModel model : pickerAttachment) {
         if (model.getSource() == MediaAttachment.Source.CAMERA) {
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
      for (BaseMediaPickerViewModel model : pickerAttachment) {
         MediaPickerModelImpl mediaPickerModel = null;
         if (model.getType() == MediaPickerModel.Type.VIDEO) {
            mediaPickerModel = new VideoPickerModel(model.getAbsolutePath(), ((GalleryVideoPickerViewModel) model).getDuration());
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

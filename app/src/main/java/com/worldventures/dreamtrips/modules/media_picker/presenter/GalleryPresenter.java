package com.worldventures.dreamtrips.modules.media_picker.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetMediaFromGalleryCommand;
import com.worldventures.dreamtrips.modules.common.delegate.PickImageDelegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class GalleryPresenter extends BasePickerPresenter<GalleryPresenter.View> {

   @Inject PickImageDelegate pickImageDelegate;
   @Inject MediaInteractor mediaInteractor;
   private boolean videoPickingEnabled;
   private int videoLengthLimit;

   public GalleryPresenter(boolean videoPickingEnabled, int videoLengthLimit) {
      this.videoPickingEnabled = videoPickingEnabled;
      this.videoLengthLimit = videoLengthLimit;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      loadGallery();
   }

   public void onCameraIconClicked() {
      if (!videoPickingEnabled) tryOpenCameraForPhoto();
      else view.showAttachmentTypeDialog();
   }

   public void openCameraForPhoto() {
      pickImageDelegate.takePicture();
   }

   public void tryOpenCameraForPhoto() {
      view.checkPermissionsForPhoto();
   }

   public void openCameraForVideo() {
      pickImageDelegate.recordVideo(videoLengthLimit);
   }

   public void tryOpenCameraForVideo() {
      view.checkPermissionsForVideo();
   }

   public void onVideoPickerModelSelected(VideoPickerModel model) {
      if (getCheckedPhotosCount() > 0) {
         resetModelState(model);
         view.informUser(context.getString(R.string.picker_two_media_type_error));
         return;
      }
      int videoDurationSec = (int) (model.getDuration() / 1000);
      if (videoDurationSec > videoLengthLimit) {
         resetModelState(model);
         view.informUser(context.getString(R.string.picker_video_length_limit, videoLengthLimit));
         return;
      }
      MediaPickerModel previouslySelectedVideo = Queryable.from(mediaPickerModels)
            .filter(element -> element.isChecked() && !element.equals(model))
            .firstOrDefault();
      if (previouslySelectedVideo != null) resetModelState(previouslySelectedVideo);
      view.updateItem(model);
   }

   public void openFacebook() {
      resetPickedItems();
      view.updatePickedItemsCount(0);
      view.openFacebookAlbums();
   }

   private void loadGallery() {
      if (mediaPickerModels != null && mediaPickerModels.size() > 0) {
         view.addItems(mediaPickerModels);
         return;
      }
      mediaInteractor.getMediaFromGalleryPipe().createObservable(new GetMediaFromGalleryCommand(videoPickingEnabled))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetMediaFromGalleryCommand>()
                  .onSuccess(getMediaFromGalleryCommand -> updateData(getMediaFromGalleryCommand.getResult())));
   }

   private void updateData(List<MediaPickerModel> models) {
      mediaPickerModels = new ArrayList<>(models);
      view.addItems(mediaPickerModels);
   }

   private void resetPickedItems() {
      Queryable.from(mediaPickerModels).filter(MediaPickerModel::isChecked).forEachR(photo -> photo.setChecked(false));
   }

   public interface View extends BasePickerPresenter.View {
      void showAttachmentTypeDialog();

      void openFacebookAlbums();

      void checkPermissionsForPhoto();

      void checkPermissionsForVideo();
   }
}

package com.worldventures.dreamtrips.modules.media_picker.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

import static com.worldventures.dreamtrips.modules.common.util.MediaPickerConstants.MAX_VIDEO_DURATION_SEC;

public abstract class BasePickerPresenter<T extends BasePickerPresenter.View> extends Presenter<T> {

   @State protected ArrayList<MediaPickerModel> mediaPickerModels;
   private int photoPickLimit;

   public BasePickerPresenter() {
      this.mediaPickerModels = new ArrayList<>();
   }

   public void onPhotoPickerModelSelected(MediaPickerModel model) {
      if (getCheckedVideosCount() > 0) {
         resetModelState(model);
         view.informUser(context.getString(R.string.picker_erros_photos_and_videos));
         return;
      }
      if (!view.isPhotosMultiPickEnabled()) {
         MediaPickerModel previouslySelectedPhoto = Queryable.from(mediaPickerModels)
               .filter(element -> element.isChecked() && !element.equals(model))
               .firstOrDefault();
         if (previouslySelectedPhoto != null) resetModelState(previouslySelectedPhoto);
      } else {
         if (isPhotoPickLimitReached(getCheckedModelsCount())) {
            model.setChecked(false);
            view.informUser(String.format(context.getResources()
                  .getString(R.string.photo_limitation_message), photoPickLimit));
         }
      }
      view.updatePickedItemsCount(Queryable.from(mediaPickerModels).count(MediaPickerModel::isChecked));
      view.updateItem(model);
   }

   public void onVideoPickerModelSelected(VideoPickerModel model) {
      if (getCheckedPhotosCount() > 0) {
         resetModelState(model);
         view.informUser(context.getString(R.string.picker_erros_photos_and_videos));
         return;
      }
      int videoDurationSec = (int) (model.getDuration() / 1000);
      if (videoDurationSec > MAX_VIDEO_DURATION_SEC) {
         resetModelState(model);
         view.informUser(context.getString(R.string.picker_video_duration_limit, MAX_VIDEO_DURATION_SEC));
         return;
      }
      MediaPickerModel previouslySelectedVideo = Queryable.from(mediaPickerModels)
            .filter(element -> element.isChecked() && !element.equals(model))
            .firstOrDefault();
      if (previouslySelectedVideo != null) resetModelState(previouslySelectedVideo);
      view.updateItem(model);
   }

   private void resetModelState(MediaPickerModel model) {
      model.setChecked(false);
      view.updateItem(model);
   }

   private int getCheckedModelsCount() {
      return Queryable.from(mediaPickerModels).count(MediaPickerModel::isChecked);
   }

   private int getCheckedVideosCount() {
      return Queryable.from(mediaPickerModels).count(element -> element.isChecked()
            && element.getType() == MediaPickerModel.Type.VIDEO);
   }

   private int getCheckedPhotosCount() {
      return Queryable.from(mediaPickerModels).count(element -> element.isChecked()
            && element.getType() == MediaPickerModel.Type.PHOTO);
   }

   public List<MediaPickerModel> getSelectedPhotos() {
      return getSelectedItems(MediaPickerModel.Type.PHOTO).toList();
   }

   public VideoPickerModel getSelectedVideo() {
      return getSelectedItems(MediaPickerModel.Type.VIDEO).cast(VideoPickerModel.class).firstOrDefault();
   }

   public Queryable<MediaPickerModel> getSelectedItems(MediaPickerModel.Type type) {
      return Queryable.from(mediaPickerModels).filter(model -> model.isChecked() && model.getType() == type)
            .sort((lhs, rhs) -> lhs.getPickedTime() > rhs.getPickedTime() ? 1 : lhs.getPickedTime() < rhs.getPickedTime() ? -1 : 0);
   }

   public void setPhotoPickLimit(int pickLimit) {
      this.photoPickLimit = pickLimit;
   }

   private boolean isPhotoPickLimitReached(int pickedCount) {
      return photoPickLimit != 0 && pickedCount > photoPickLimit;
   }

   public interface View extends Presenter.View {

      void updateItem(MediaPickerModel item);

      void addItems(List<MediaPickerModel> items);

      void updatePickedItemsCount(int count);

      boolean isPhotosMultiPickEnabled();
   }
}

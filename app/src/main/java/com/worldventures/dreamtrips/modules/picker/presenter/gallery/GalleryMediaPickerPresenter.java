package com.worldventures.dreamtrips.modules.picker.presenter.gallery;


import com.worldventures.dreamtrips.modules.picker.model.GalleryMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.base.BaseMediaPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.util.strategy.PhotoPickLimitStrategy;
import com.worldventures.dreamtrips.modules.picker.util.strategy.VideoPickLimitStrategy;
import com.worldventures.dreamtrips.modules.picker.view.gallery.GalleryMediaPickerView;

import java.util.List;

import rx.Observable;

public interface GalleryMediaPickerPresenter extends BaseMediaPickerPresenter<GalleryMediaPickerView, GalleryMediaPickerViewModel> {

   void tryOpenCameraForPhoto();

   void openCameraForPhoto();

   void tryOpenCameraForVideo();

   void openCameraForVideo();

   void attachMedia();

   void handleCameraClick();

   boolean validateItemPick(GalleryMediaPickerViewModel pickedItem, VideoPickLimitStrategy videoPickLimitStrategy,
         PhotoPickLimitStrategy photoPickLimitStrategy);

   Observable<List<GalleryMediaPickerViewModel>> capturedMedia();
}

package com.worldventures.core.modules.picker.presenter.gallery;


import com.worldventures.core.modules.picker.viewmodel.GalleryMediaPickerViewModel;
import com.worldventures.core.modules.picker.presenter.base.BaseMediaPickerPresenter;
import com.worldventures.core.modules.picker.util.strategy.PhotoPickLimitStrategy;
import com.worldventures.core.modules.picker.util.strategy.VideoPickLimitStrategy;
import com.worldventures.core.modules.picker.view.gallery.GalleryMediaPickerView;

public interface GalleryMediaPickerPresenter extends BaseMediaPickerPresenter<GalleryMediaPickerView, GalleryMediaPickerViewModel> {

   void tryOpenCameraForPhoto();

   void openCameraForPhoto();

   void tryOpenCameraForVideo();

   void openCameraForVideo();

   void handleCameraClick();

   void itemPicked(GalleryMediaPickerViewModel pickedItem, int position,
         VideoPickLimitStrategy videoPickLimitStrategy, PhotoPickLimitStrategy photoPickLimitStrategy);
}

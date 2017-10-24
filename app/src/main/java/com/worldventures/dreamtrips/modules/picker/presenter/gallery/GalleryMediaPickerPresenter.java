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

   void handleCameraClick();

   void itemPicked(GalleryMediaPickerViewModel pickedItem, int position,
         VideoPickLimitStrategy videoPickLimitStrategy, PhotoPickLimitStrategy photoPickLimitStrategy);
}

package com.worldventures.core.modules.picker.view.facebook.photos;

import com.worldventures.core.modules.facebook.service.command.GetPhotosCommand;
import com.worldventures.core.modules.picker.view.facebook.FacebookMediaPickerView;
import com.worldventures.core.modules.picker.viewmodel.FacebookPhotoPickerViewModel;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;


public interface FacebookPhotosPickerView extends FacebookMediaPickerView<FacebookPhotoPickerViewModel> {

   String getAlbumId();

   int getPickLimit();

   List<FacebookPhotoPickerViewModel> getChosenPhotos();

   OperationView<GetPhotosCommand> provideOperationGetPhotos();
}

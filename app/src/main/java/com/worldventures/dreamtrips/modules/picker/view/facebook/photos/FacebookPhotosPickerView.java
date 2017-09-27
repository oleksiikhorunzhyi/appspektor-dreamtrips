package com.worldventures.dreamtrips.modules.picker.view.facebook.photos;

import com.worldventures.dreamtrips.modules.facebook.service.command.GetPhotosCommand;
import com.worldventures.dreamtrips.modules.picker.model.FacebookPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.view.facebook.FacebookMediaPickerView;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;


public interface FacebookPhotosPickerView extends FacebookMediaPickerView<FacebookPhotoPickerViewModel> {

   String getAlbumId();

   int getPickLimit();

   List<FacebookPhotoPickerViewModel> getChosenPhotos();

   OperationView<GetPhotosCommand> provideOperationGetPhotos();
}

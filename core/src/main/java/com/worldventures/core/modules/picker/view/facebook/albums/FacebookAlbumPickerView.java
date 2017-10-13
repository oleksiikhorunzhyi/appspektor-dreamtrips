package com.worldventures.core.modules.picker.view.facebook.albums;

import com.worldventures.core.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.core.modules.picker.view.facebook.FacebookMediaPickerView;
import com.worldventures.core.modules.picker.viewmodel.FacebookAlbumPickerViewModel;

import io.techery.janet.operationsubscriber.view.OperationView;


public interface FacebookAlbumPickerView extends FacebookMediaPickerView<FacebookAlbumPickerViewModel> {

   OperationView<GetAlbumsCommand> provideOperationGetAlbums();

   void goBack();
}

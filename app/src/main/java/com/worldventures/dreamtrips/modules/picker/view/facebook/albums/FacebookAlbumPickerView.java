package com.worldventures.dreamtrips.modules.picker.view.facebook.albums;

import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.modules.picker.model.FacebookAlbumPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.view.facebook.FacebookMediaPickerView;

import io.techery.janet.operationsubscriber.view.OperationView;


public interface FacebookAlbumPickerView extends FacebookMediaPickerView<FacebookAlbumPickerViewModel> {

   OperationView<GetAlbumsCommand> provideOperationGetAlbums();

   void goBack();
}

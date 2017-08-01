package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import android.support.annotation.NonNull;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.SuggestedPhotoCellPresenterHelper;
import com.worldventures.dreamtrips.modules.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;

public interface SuggestedPhotosDelegate extends CellDelegate<SuggestedPhotosCell.SuggestedPhotoModel> {

   void onCancelClicked();

   void onAttachClicked();

   void onOpenProfileClicked();

   void onSuggestionViewCreated(@NonNull SuggestedPhotoCellPresenterHelper.View view);

   void onSyncViewState();

   void onPreloadSuggestionPhotos(@NonNull PhotoPickerModel model);

   void onSelectPhoto(@NonNull PhotoPickerModel model);

   long lastSyncTimestamp();
}
package com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate;

import android.support.annotation.NonNull;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.feed.presenter.SuggestedPhotoCellPresenterHelper;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.SuggestedPhotosCell;

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
package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import android.support.annotation.NonNull;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.feed.presenter.SuggestedPhotoCellPresenterHelper;

public interface SuggestedPhotosDelegate extends CellDelegate<MediaAttachment> {

   void onCancelClicked();

   void onAttachClicked();

   void onOpenProfileClicked();

   void onSuggestionViewCreated(@NonNull SuggestedPhotoCellPresenterHelper.View view);

   void onSyncViewState();

   void onPreloadSuggestionPhotos(@NonNull PhotoGalleryModel model);

   void onSelectPhoto(@NonNull PhotoGalleryModel model);

   long lastSyncTimestamp();
}
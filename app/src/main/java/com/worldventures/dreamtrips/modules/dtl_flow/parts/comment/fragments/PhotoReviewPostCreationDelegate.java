package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;

public interface PhotoReviewPostCreationDelegate extends CellDelegate<PhotoReviewCreationItem> {

   void onTagIconClicked(PhotoReviewCreationItem item);

   void onRemoveClicked(PhotoReviewCreationItem item);

   void onSuggestionClicked(PhotoReviewCreationItem item, PhotoTag tag);

   void onPhotoTitleChanged(String title);

   void onPhotoTitleFocusChanged(boolean hasFocus);

   void onTagsChanged();
}

package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;

public interface PhotoReviewPostCreationDelegate extends CellDelegate<PhotoReviewCreationItem> {

   void onTagIconClicked(PhotoReviewCreationItem item);

   void onRemoveClicked(PhotoReviewCreationItem item);

   void onSuggestionClicked(PhotoReviewCreationItem item, PhotoTag tag);

   void onPhotoTitleChanged(String title);

   void onPhotoTitleFocusChanged(boolean hasFocus);

   void onTagsChanged();
}

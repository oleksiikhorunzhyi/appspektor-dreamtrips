package com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoCreationItem;

public interface PhotoPostCreationDelegate extends CellDelegate<PhotoCreationItem> {

   void onTagIconClicked(PhotoCreationItem item);

   void onRemoveClicked(PhotoCreationItem item);

   void onSuggestionClicked(PhotoCreationItem item, PhotoTag tag);

   void onPhotoTitleChanged(String title);

   void onPhotoTitleFocusChanged(boolean hasFocus);

   void onTagsChanged();
}

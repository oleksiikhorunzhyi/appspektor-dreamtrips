package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;

public interface PhotoPostCreationDelegate extends CellDelegate<PhotoCreationItem> {

   void onProgressClicked(PhotoCreationItem item);

   void onTagIconClicked(PhotoCreationItem item);

   void onRemoveClicked(PhotoCreationItem item);

   void onSuggestionClicked(PhotoCreationItem item, PhotoTag tag);

   void onPhotoTitleChanged(String title);

   void onPhotoTitleFocusChanged(boolean hasFocus);

   void onTagsChanged();
}

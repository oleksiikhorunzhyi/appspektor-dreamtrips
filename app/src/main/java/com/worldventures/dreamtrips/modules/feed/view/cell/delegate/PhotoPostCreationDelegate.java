package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

public interface PhotoPostCreationDelegate extends CellDelegate<PhotoCreationItem> {

    void onProgressClicked(PhotoCreationItem item);

    void onTagIconClicked(PhotoCreationItem item);

    void onRemoveClicked(PhotoCreationItem item);

    void onSuggestionClicked(PhotoCreationItem item, PhotoTag tag);
}

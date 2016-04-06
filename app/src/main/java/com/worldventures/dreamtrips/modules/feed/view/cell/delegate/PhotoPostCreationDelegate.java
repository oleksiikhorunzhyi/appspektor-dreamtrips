package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;

public interface PhotoPostCreationDelegate extends CellDelegate<PhotoCreationItem> {

    void onProgressClicked(PhotoCreationItem uploadTask);

    void onTagClicked(PhotoCreationItem uploadTask);

    void onRemoveClicked(PhotoCreationItem uploadTask);
}

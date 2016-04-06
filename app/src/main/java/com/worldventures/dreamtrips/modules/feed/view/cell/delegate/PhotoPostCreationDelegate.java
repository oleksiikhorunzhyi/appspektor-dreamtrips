package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public interface PhotoPostCreationDelegate extends CellDelegate<UploadTask> {

    void onProgressClicked(UploadTask uploadTask);

    void onTagClicked(UploadTask uploadTask);

    void onRemoveClicked(UploadTask uploadTask);
}

package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import android.database.ContentObserver;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;

import java.util.List;

public interface SuggestedPhotosDelegate extends CellDelegate<MediaAttachment> {
    void onCancelClicked();

    void onAttachClicked(List<PhotoGalleryModel> pickedItems);

    void onRegisterObserver(ContentObserver contentObserver);
}
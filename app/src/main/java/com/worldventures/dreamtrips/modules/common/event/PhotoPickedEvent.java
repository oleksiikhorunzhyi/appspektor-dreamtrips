package com.worldventures.dreamtrips.modules.common.event;

import com.worldventures.dreamtrips.modules.feed.model.PhotoGalleryModel;

public class PhotoPickedEvent {

    public final PhotoGalleryModel model;

    public PhotoPickedEvent(PhotoGalleryModel model) {
        this.model = model;
    }
}

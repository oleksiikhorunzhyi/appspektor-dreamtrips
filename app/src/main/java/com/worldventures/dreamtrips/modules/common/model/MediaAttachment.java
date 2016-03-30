package com.worldventures.dreamtrips.modules.common.model;

import java.util.List;

public class MediaAttachment {

    public final List<PhotoGalleryModel> chosenImages;
    public final int type;
    public final int requestId;

    public MediaAttachment(List<PhotoGalleryModel> chosenImages, int type, int requestId) {
        this.chosenImages = chosenImages;
        this.type = type;
        this.requestId = requestId;
    }
}

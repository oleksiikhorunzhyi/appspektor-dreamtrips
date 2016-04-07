package com.worldventures.dreamtrips.modules.common.model;

import com.kbeanie.imagechooser.api.ChosenImage;

import java.util.List;

public class MediaAttachment {

    public final List<ChosenImage> chosenImages;
    public final int type;
    public final int requestId;

    public MediaAttachment(List<ChosenImage> chosenImages, int type, int requestId) {
        this.chosenImages = chosenImages;
        this.type = type;
        this.requestId = requestId;
    }
}

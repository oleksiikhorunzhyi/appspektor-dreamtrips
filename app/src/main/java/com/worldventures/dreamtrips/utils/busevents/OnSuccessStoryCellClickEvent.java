package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.model.SuccessStory;

public class OnSuccessStoryCellClickEvent {
    private SuccessStory modelObject;
    private int position;

    public OnSuccessStoryCellClickEvent(SuccessStory modelObject, int position) {
        this.modelObject = modelObject;
        this.position = position;
    }

    public SuccessStory getModelObject() {
        return modelObject;
    }

    public int getPosition() {
        return position;
    }
}
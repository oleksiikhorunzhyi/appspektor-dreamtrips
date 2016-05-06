package com.worldventures.dreamtrips.modules.tripsimages.model;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

public class AddPhotoTag {

    private List<PhotoTag> tags;

    public AddPhotoTag() {
        tags = new ArrayList<>();
    }

    public AddPhotoTag(List<PhotoTag> tags) {
        this.tags = tags;
    }

    public void addTag(PhotoTag tag) {
        tags.add(tag);
    }

}

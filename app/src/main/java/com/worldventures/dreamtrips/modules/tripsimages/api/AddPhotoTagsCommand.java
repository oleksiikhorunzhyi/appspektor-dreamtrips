package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddPhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.List;

public class AddPhotoTagsCommand extends Command<Void> {

    private String photoId;
    private List<PhotoTag> tags;

    public AddPhotoTagsCommand(String photoId, List<PhotoTag> tags) {
        super(Void.class);
        this.photoId = photoId;
        this.tags = tags;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().addPhotoTags(photoId, new AddPhotoTag(tags));
    }
}

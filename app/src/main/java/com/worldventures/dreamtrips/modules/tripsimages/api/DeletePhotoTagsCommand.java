package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.tripsimages.model.DeletePhotoTag;

import java.util.List;

public class DeletePhotoTagsCommand extends Command<Void> {

    private String photoId;
    private List<Integer> userIds;

    public DeletePhotoTagsCommand(String photoId, List<Integer> userIds) {
        super(Void.class);
        this.photoId = photoId;
        this.userIds = userIds;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().deletePhotoTags(photoId, new DeletePhotoTag(userIds));
    }
}

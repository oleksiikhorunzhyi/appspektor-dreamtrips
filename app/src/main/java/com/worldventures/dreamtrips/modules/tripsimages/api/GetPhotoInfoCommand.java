package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class GetPhotoInfoCommand extends Command<Photo> {

    private String uid;

    public GetPhotoInfoCommand(String uid) {
        super(Photo.class);
        this.uid = uid;
    }

    @Override
    public Photo loadDataFromNetwork() throws Exception {
        return getService().getPhotoInfo(uid);
    }
}
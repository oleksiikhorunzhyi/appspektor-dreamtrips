package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;

public class CreatePhotoPostCommand extends Command<Void> {

    private CreatePhotoPostEntity createPhotoPostEntity;

    public CreatePhotoPostCommand(CreatePhotoPostEntity createPhotoPostEntity) {
        super(Void.class);
        this.createPhotoPostEntity = createPhotoPostEntity;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().createPhotoPost(createPhotoPostEntity);
    }
}

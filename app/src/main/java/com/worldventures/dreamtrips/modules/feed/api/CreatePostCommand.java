package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class CreatePostCommand extends Command<FeedEntity> {

    private CreatePhotoPostEntity createPhotoPostEntity;

    public CreatePostCommand(CreatePhotoPostEntity createPhotoPostEntity) {
        super(FeedEntity.class);
        this.createPhotoPostEntity = createPhotoPostEntity;
    }

    @Override
    public FeedEntity loadDataFromNetwork() throws Exception {
        return getService().createPhotoPost(createPhotoPostEntity);
    }
}

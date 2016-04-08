package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

public class PreviewPhotoTaggableHolderPresenter extends TaggableImageHolderPresenter<PreviewPhotoTaggableHolderPresenter.View> {

    public PreviewPhotoTaggableHolderPresenter(Photo photo) {
        super(photo);
    }

    @Override
    public void deletePhotoTag(PhotoTag tag) {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(tag.getTargetUserId());
        doRequest(new DeletePhotoTagsCommand(photo.getFSId(), userIds), aVoid -> {
            photo.getPhotoTags().remove(tag);
            view.onTagDeleted();
        });
    }

    public interface View extends TaggableImageHolderPresenter.View {
        void onTagDeleted();
    }

}

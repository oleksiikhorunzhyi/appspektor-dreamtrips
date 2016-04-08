package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.List;

public class InsertNewImageUploadTaskEvent {

    private UploadTask uploadTask;
    private List<PhotoTag> photoTags;

    public InsertNewImageUploadTaskEvent(UploadTask uploadTask, List<PhotoTag> photoTags) {
        this.uploadTask = uploadTask;
        this.photoTags = photoTags;
    }

    public UploadTask getUploadTask() {
        return uploadTask;
    }

    public List<PhotoTag> getPhotoTags() {
        return photoTags;
    }
}

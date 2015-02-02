package com.worldventures.dreamtrips.utils.busevents;

import android.util.Log;

import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.uploader.job.UploadJob;

public class PhotoUploadFinished {
    private Photo photo;

    public PhotoUploadFinished(Photo photo) {
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }
}

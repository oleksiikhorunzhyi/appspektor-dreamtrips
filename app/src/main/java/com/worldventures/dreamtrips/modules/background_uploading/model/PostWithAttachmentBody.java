package com.worldventures.dreamtrips.modules.background_uploading.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PostWithAttachmentBody {
   @Nullable String text();
   @Nullable Location location();
   @Nullable List<PhotoAttachment> attachments();
   @Nullable List<Photo> uploadedPhotos();
}

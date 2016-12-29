package com.worldventures.dreamtrips.modules.background_uploading.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import org.immutables.value.Value;

import java.util.ArrayList;

@Value.Immutable
public interface PostWithAttachmentBody {
   @Nullable String text();
   @Nullable Location location();
   ArrayList<PhotoAttachment> attachments();
   @Nullable ArrayList<Photo> uploadedPhotos();
   @Nullable TextualPost createdPost();
   CreateEntityBundle.Origin origin();
}

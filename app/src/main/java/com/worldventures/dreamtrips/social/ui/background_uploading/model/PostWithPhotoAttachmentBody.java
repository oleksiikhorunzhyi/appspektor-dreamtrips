package com.worldventures.dreamtrips.social.ui.background_uploading.model;


import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import org.immutables.value.Value;

import java.util.ArrayList;

@Value.Immutable
public interface PostWithPhotoAttachmentBody extends PostBody {

   ArrayList<PhotoAttachment> attachments();
   @Nullable
   ArrayList<Photo> uploadedPhotos();

}

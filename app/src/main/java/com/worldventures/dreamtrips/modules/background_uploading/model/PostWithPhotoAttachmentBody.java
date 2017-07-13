package com.worldventures.dreamtrips.modules.background_uploading.model;


import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import org.immutables.value.Value;

import java.util.ArrayList;

@Value.Immutable
public interface PostWithPhotoAttachmentBody extends PostBody {

   ArrayList<PhotoAttachment> attachments();
   @Nullable
   ArrayList<Photo> uploadedPhotos();

}

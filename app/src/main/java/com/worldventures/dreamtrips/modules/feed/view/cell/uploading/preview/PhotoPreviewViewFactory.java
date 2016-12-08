package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.content.Context;

import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;

import java.util.List;

public class PhotoPreviewViewFactory {

   public static PhotoAttachmentPreviewView provideView(Context context, List<PhotoAttachment> attachments) {

        switch (attachments.size()) {
           case 1:
              return new SinglePhotoAttachmentPreviewView(context);
           case 2:
              return new DoublePhotoAttachmentPreviewView(context);
           case 3:
              return new TriplePhotoAttachmentPreviewView(context);
           default:
              return new MultiplePhotoAttachmentPreviewView(context);
        }
   }
}

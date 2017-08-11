package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.content.Context;

public class PhotoPreviewViewFactory {

   public static PhotoAttachmentPreviewView provideView(Context context, int attachmentsCount) {
        switch (attachmentsCount) {
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

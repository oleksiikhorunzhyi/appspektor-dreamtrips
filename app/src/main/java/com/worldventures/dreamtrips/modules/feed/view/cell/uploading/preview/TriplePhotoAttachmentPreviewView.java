package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.content.Context;

import com.worldventures.dreamtrips.R;

public class TriplePhotoAttachmentPreviewView extends BasePhotoAttachmentsPreviewView {

   public TriplePhotoAttachmentPreviewView(Context context) {
      super(context);
   }

   @Override
   protected int[] getPreviewViewsIds() {
      return new int[] {R.id.photo_attachment_preview_1, R.id.photo_attachment_preview_2,
            R.id.photo_attachment_preview_3};
   }

   @Override
   protected int getLayoutId() {
      return R.layout.view_upload_triple_photo_attachment;
   }
}

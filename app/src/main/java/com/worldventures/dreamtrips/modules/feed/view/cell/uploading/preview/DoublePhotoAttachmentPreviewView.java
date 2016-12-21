package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.content.Context;

import com.worldventures.dreamtrips.R;

public class DoublePhotoAttachmentPreviewView extends BasePhotoAttachmentsPreviewView {

   public DoublePhotoAttachmentPreviewView(Context context) {
      super(context);
   }

   protected int[] getPreviewViewsIds() {
      return new int[] {R.id.photo_attachment_preview_1, R.id.photo_attachment_preview_2};
   }

   protected int getLayoutId() {
      return R.layout.view_double_photo_attachment;
   }
}

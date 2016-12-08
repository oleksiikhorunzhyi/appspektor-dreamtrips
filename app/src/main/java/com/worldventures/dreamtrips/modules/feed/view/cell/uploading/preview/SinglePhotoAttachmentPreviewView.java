package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;

import java.util.List;

public class SinglePhotoAttachmentPreviewView implements PhotoAttachmentPreviewView {
   private SimpleDraweeView simpleDraweeView;

   public SinglePhotoAttachmentPreviewView(Context context) {
      simpleDraweeView = new SimpleDraweeView(context);
   }

   @Override
   public void showPreview(List<PhotoAttachment> attachments) {
      simpleDraweeView.setImageURI(Uri.parse(attachments.get(0).originUrl()));
   }

   @Override
   public void attachView(ViewGroup viewGroup) {
      viewGroup.addView(simpleDraweeView);
   }
}

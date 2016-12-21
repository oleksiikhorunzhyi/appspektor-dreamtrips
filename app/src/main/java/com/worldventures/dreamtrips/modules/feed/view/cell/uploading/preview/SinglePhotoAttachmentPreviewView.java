package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;

import java.io.File;
import java.util.List;

public class SinglePhotoAttachmentPreviewView extends BasePhotoAttachmentPreviewView {

   private SimpleDraweeView simpleDraweeView;

   public SinglePhotoAttachmentPreviewView(Context context) {
      super(context);
   }

   @Override
   public void attachView(ViewGroup viewGroup) {
      rootView = simpleDraweeView = new SimpleDraweeView(context);
      viewGroup.addView(simpleDraweeView);
   }

   @Override
   public void showPreview(List<PhotoAttachment> attachments, boolean animate) {
      super.showPreview(attachments, animate);
      if (simpleDraweeView.getParent() == null) throw new IllegalStateException("Must call attachView() first");
      simpleDraweeView.setImageURI(Uri.fromFile(new File(attachments.get(0).selectedPhoto().path())));
   }
}

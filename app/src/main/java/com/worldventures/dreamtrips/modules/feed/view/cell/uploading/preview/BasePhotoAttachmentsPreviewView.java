package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePhotoAttachmentsPreviewView extends BasePhotoAttachmentPreviewView {

   protected List<SimpleDraweeView> previewViews = new ArrayList<>();

   public BasePhotoAttachmentsPreviewView(Context context) {
      super(context);
   }

   private void init() {
      rootView = LayoutInflater.from(context).inflate(getLayoutId(), null, false);
      for (int id : getPreviewViewsIds()) {
         previewViews.add((SimpleDraweeView) rootView.findViewById(id));
      }
      onViewCreated();
   }

   @Override
   public void attachView(ViewGroup viewGroup) {
      init();
      viewGroup.addView(rootView);
   }

   @Override
   public void showPreview(List<PhotoAttachment> attachments, boolean animate) {
      super.showPreview(attachments, animate);
      int attachmentsLastElementIndex = attachments.size() - 1;
      for (int i = 0; i < previewViews.size(); i++) {
         if (i > attachmentsLastElementIndex) {
            break;
         }
         PhotoAttachment photoAttachment = attachments.get(i);
         previewViews.get(i).setImageURI(Uri.fromFile(new File(photoAttachment.selectedPhoto().path())));
      }
   }

   protected void onViewCreated() {
   }

   protected abstract int[] getPreviewViewsIds();

   protected abstract int getLayoutId();
}

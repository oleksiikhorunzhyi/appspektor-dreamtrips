package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MultiplePhotoAttachmentPreviewView extends BasePhotoAttachmentsPreviewView {

   private static final int MAX_DISPLAY_COUNT = 4;

   @InjectView(R.id.photo_attachment_additional_count_textview) TextView additionalCountTextView;

   public MultiplePhotoAttachmentPreviewView(Context context) {
      super(context);
   }

   @Override
   protected int[] getPreviewViewsIds() {
      return new int[] {R.id.photo_attachment_preview_1, R.id.photo_attachment_preview_2,
            R.id.photo_attachment_preview_3, R.id.photo_attachment_preview_4};
   }

   @Override
   protected int getLayoutId() {
      return R.layout.view_multiple_photo_attachment;
   }

   @Override
   public void showPreview(List<PhotoAttachment> attachments) {
      super.showPreview(attachments);
      if (attachments.size() > MAX_DISPLAY_COUNT) {
         additionalCountTextView.setText(String.format("+%d", attachments.size() - MAX_DISPLAY_COUNT));
         additionalCountTextView.setVisibility(View.VISIBLE);
      } else {
         additionalCountTextView.setVisibility(View.GONE);
      }
   }

   @Override
   protected void onViewCreated() {
      super.onViewCreated();
      ButterKnife.inject(this, view);
   }
}

package com.worldventures.dreamtrips.social.ui.feed.view.cell.uploading.preview;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.feed.view.util.blur.BlurPostprocessor;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MultiplePhotoAttachmentPreviewView extends BasePhotoAttachmentsPreviewView {

   private static final int MAX_DISPLAY_COUNT = 4;
   private static final int BLUR_PREVIEW_POSITION = 3;

   private static final int BLUR_RADIUS = 30;
   private static final int BLUR_SAMPLING = 1;

   @InjectView(R.id.photo_attachment_additional_count_textview) TextView additionalCountTextView;

   public MultiplePhotoAttachmentPreviewView(Context context) {
      super(context);
   }

   @Override
   protected int[] getPreviewViewsIds() {
      return new int[]{R.id.photo_attachment_preview_1, R.id.photo_attachment_preview_2,
            R.id.photo_attachment_preview_3, R.id.photo_attachment_preview_4};
   }

   @Override
   protected int getLayoutId() {
      return R.layout.view_multiple_photo_attachment;
   }

   @Override
   public void showPreview(List<Uri> attachments, boolean animate) {
      super.showPreview(attachments, animate);
      refreshPreviewImages(attachments);
      refreshUploadsLeftCount(attachments);
   }

   private void refreshPreviewImages(List<Uri> attachments) {
      int attachmentsLastElementIndex = attachments.size() - 1;
      for (int i = 0; i < previewViews.size(); i++) {
         if (i > attachmentsLastElementIndex) {
            break;
         }
         refreshPreviewImage(i, attachments);
      }
   }

   private void refreshPreviewImage(int index, List<Uri> attachments) {
      Uri uri = attachments.get(index);
      SimpleDraweeView view = previewViews.get(index);

      ImageRequestBuilder imageRequest = ImageRequestBuilder.newBuilderWithSource(uri);
      boolean blurImage = false;
      if (attachments.size() > MAX_DISPLAY_COUNT && index == BLUR_PREVIEW_POSITION) {
         imageRequest.setPostprocessor(new BlurPostprocessor(context, BLUR_RADIUS, BLUR_SAMPLING));
         if (view.getMeasuredWidth() == 0) {
            view.measure(0, 0);
         }
         imageRequest.setResizeOptions(new ResizeOptions(view.getMeasuredWidth(), view.getMeasuredHeight()));
         blurImage = true;
      }
      imageRequest.build();

      BlurInfo blurInfo = new BlurInfo(uri, index, blurImage);
      BlurInfo oldBlurInfo = (BlurInfo) view.getTag();
      if (oldBlurInfo != null && oldBlurInfo.equals(blurInfo)) {
         return;
      }
      // cache old image info to not trigger creating and rendering blurred image again
      // as this is slow process and setting old controller does not help.
      view.setTag(blurInfo);

      PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest.build());
      if (view.getController() != null) {
         controller.setOldController(view.getController());
      }
      view.setController(controller.build());
   }

   private void refreshUploadsLeftCount(List<Uri> attachments) {
      if (attachments.size() > MAX_DISPLAY_COUNT) {
         // blurred image does not contribute to images left to upload counter
         additionalCountTextView.setText(String.format("+%d", attachments.size() - MAX_DISPLAY_COUNT + 1));
         additionalCountTextView.setVisibility(View.VISIBLE);
      } else {
         additionalCountTextView.setVisibility(View.GONE);
      }
   }

   @Override
   protected void onViewCreated() {
      super.onViewCreated();
      ButterKnife.inject(this, rootView);
   }

   private static class BlurInfo {
      private Uri uri;
      private int position;
      private boolean isBlurred;

      public BlurInfo(Uri uri, int position, boolean isBlurred) {
         this.uri = uri;
         this.position = position;
         this.isBlurred = isBlurred;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         BlurInfo blurInfo = (BlurInfo) o;

         if (position != blurInfo.position) return false;
         if (isBlurred != blurInfo.isBlurred) return false;
         return uri != null ? uri.equals(blurInfo.uri) : blurInfo.uri == null;

      }

      @Override
      public int hashCode() {
         int result = uri != null ? uri.hashCode() : 0;
         result = 31 * result + position;
         result = 31 * result + (isBlurred ? 1 : 0);
         return result;
      }
   }
}

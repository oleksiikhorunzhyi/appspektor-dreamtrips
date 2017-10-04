package com.worldventures.dreamtrips.social.ui.feed.view.cell;


import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoMetadata;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.VideoCreationCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video_post)
public class VideoPostCreationCell extends BaseAbstractDelegateCell<VideoCreationModel, VideoCreationCellDelegate> implements ResizeableCell {

   @InjectView(R.id.video_thumbnail) SimpleDraweeView videoThumbnail;
   @InjectView(R.id.remove) View remove;

   private volatile int previousWidth;
   private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

   public VideoPostCreationCell(View view) {
      super(view);

      globalLayoutListener = () -> {
         if (itemView.getWidth() != previousWidth) {
            refreshParamsForAspectRatio(getModelObject().videoMetadata());
         }
      };
   }

   @Override
   protected void onAttachedToWindow(View v) {
      super.onAttachedToWindow(v);
      itemView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
   }

   @Override
   public void clearResources() {
      super.clearResources();
      itemView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
   }

   @Override
   protected void syncUIStateWithModel() {
      remove.setVisibility(getModelObject().canDelete() ? View.VISIBLE : View.GONE);
      ViewUtils.runTaskAfterMeasure(itemView, () -> {
               VideoCreationModel videoCreationModel = getModelObject();
               if (videoCreationModel.videoMetadata() != null) {
                  refreshParamsForAspectRatio(videoCreationModel.videoMetadata());
               } else {
                  assignDefaultLayoutParams();
               }
               Uri uri = getModelObject().uri();
               videoThumbnail.setController(GraphicUtils.provideFrescoResizingController(uri,
                     videoThumbnail.getController()));
            }
      );
   }

   @Override
   public void checkSize() {
      if (itemView.getWidth() != previousWidth) {
         refreshParamsForAspectRatio(getModelObject().videoMetadata());
      }
   }

   public void refreshParamsForAspectRatio(VideoMetadata videoMetadata) {
      double aspectRatio = videoMetadata.aspectRatio();
      int videoWidth = videoMetadata.width();
      int previewWidth = videoWidth != 0 && videoWidth < itemView.getWidth() ? videoWidth : itemView.getWidth();
      int previewHeight = (int) (previewWidth / aspectRatio);

      ViewGroup.LayoutParams params = videoThumbnail.getLayoutParams();
      params.width = previewWidth;
      params.height = previewHeight;
      videoThumbnail.setLayoutParams(params);

      previousWidth = itemView.getWidth();
   }

   public void assignDefaultLayoutParams() {
      ViewGroup.LayoutParams params = videoThumbnail.getLayoutParams();
      params.width = ViewGroup.LayoutParams.MATCH_PARENT;
      params.height = ViewGroup.LayoutParams.MATCH_PARENT;
      videoThumbnail.setLayoutParams(params);
   }

   @OnClick(R.id.remove)
   void onRemove() {
      if (cellDelegate != null) {
         cellDelegate.onRemoveClicked(getModelObject());
      }
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}

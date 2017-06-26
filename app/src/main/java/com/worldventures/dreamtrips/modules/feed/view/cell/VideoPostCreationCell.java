package com.worldventures.dreamtrips.modules.feed.view.cell;


import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.VideoCreationCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video_post)
public class VideoPostCreationCell extends AbstractDelegateCell<VideoCreationModel, VideoCreationCellDelegate> {

   @InjectView(R.id.video_thumbnail) SimpleDraweeView videoThumbnail;
   @InjectView(R.id.remove) View remove;

   public VideoPostCreationCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      ViewUtils.runTaskAfterMeasure(itemView, () -> {
               VideoCreationModel videoCreationModel = getModelObject();
               if (videoCreationModel.size() != null) {
                  refreshParamsForAspectRatio(videoCreationModel.size());
               } else {
                  assignDefaultLayoutParams();
               }
               Uri uri = getModelObject().uri();
               videoThumbnail.setController(GraphicUtils.provideFrescoResizingController(uri,
                     videoThumbnail.getController()));
            }
         );
   }

   public void refreshParamsForAspectRatio(Size size) {
      int videoWidth = size.getWidth();
      int videoHeight = size.getHeight();
      float aspectRatio = (float) videoWidth / videoHeight;
      int previewWidth = 0;
      int previewHeight = 0;
      int maxCellHeight = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.video_creation_cell_height);
      if (videoWidth > videoHeight) {
         previewWidth = itemView.getWidth();
         previewHeight =  (int) (previewWidth / aspectRatio);
      } else if (videoWidth < videoHeight){
         previewHeight = maxCellHeight;
         previewWidth = (int) (previewHeight * aspectRatio);
      } else {
         previewWidth = maxCellHeight;
         previewHeight = maxCellHeight;
      }

      ViewGroup.LayoutParams params = videoThumbnail.getLayoutParams();
      params.width = previewWidth;
      params.height = previewHeight;
      videoThumbnail.setLayoutParams(params);
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
}

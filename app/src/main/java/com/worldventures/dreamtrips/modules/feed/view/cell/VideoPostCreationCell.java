package com.worldventures.dreamtrips.modules.feed.view.cell;


import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
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
      ViewUtils.runTaskAfterMeasure(videoThumbnail, () ->
         videoThumbnail.setController(GraphicUtils.provideFrescoResizingController(getModelObject().uri(),
               videoThumbnail.getController(), videoThumbnail.getWidth(), videoThumbnail.getHeight())));
   }

   @OnClick(R.id.remove)
   void onRemove() {
      if (cellDelegate != null) {
         cellDelegate.onRemoveClicked(getModelObject());
      }
   }
}

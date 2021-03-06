package com.worldventures.dreamtrips.social.ui.video.cell;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.social.ui.video.cell.util.ProgressVideoCellHelper;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video)
public class VideoCell extends BaseAbstractDelegateCell<Video, VideoCellDelegate> {

   @InjectView(R.id.iv_bg) SimpleDraweeView thumbnail;
   @InjectView(R.id.tv_title) protected TextView title;
   @InjectView(R.id.download_progress) PinProgressButton downloadProgress;

   @Inject CachedModelHelper cachedModelHelper;

   private ProgressVideoCellHelper progressVideoCellHelper;

   public VideoCell(View view) {
      super(view);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      progressVideoCellHelper = new ProgressVideoCellHelper(downloadProgress, cachedModelHelper);
   }

   @Override
   protected void syncUIStateWithModel() {
      Video video = getModelObject();

      ViewUtils.runTaskAfterMeasure(itemView, () -> {
         final PipelineDraweeController controller = GraphicUtils.provideFrescoResizingController(video.getImageUrl(),
               thumbnail.getController(), thumbnail.getWidth(), thumbnail.getHeight());
         thumbnail.setController(controller);
      });
      title.setText(video.getVideoName());

      progressVideoCellHelper.setModelObject(video.getCacheEntity());
      progressVideoCellHelper.updateButtonState();
   }

   @OnClick(R.id.iv_play)
   void onPlayClick() {
      if (cellDelegate != null) {
         cellDelegate.onPlayVideoClicked(getModelObject());
      }
   }

   @OnClick(R.id.download_progress)
   void onDownloadClick() {
      progressVideoCellHelper.onDownloadClick(cellDelegate, getModelObject());
   }

   @Override
   public void prepareForReuse() {
      thumbnail.setController(null);
   }

}

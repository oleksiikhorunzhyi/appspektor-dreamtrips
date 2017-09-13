package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video_360)
public class Video360Cell extends AbstractDelegateCell<Video, Video360Cell.Video360CellDelegate> {

   @InjectView(R.id.textViewDuration) TextView textViewDuration;
   @InjectView(R.id.tv_title) TextView textViewTitle;
   @InjectView(R.id.iv_bg) SimpleDraweeView imageViewPreview;
   @InjectView(R.id.download_progress) PinProgressButton downloadProgress;

   @Inject Context context;
   @Inject SessionHolder appSessionHolder;
   @Inject CachedModelHelper cachedModelHelper;

   private ProgressVideoCellHelper progressVideoCellHelper;

   public Video360Cell(View view) {
      super(view);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      progressVideoCellHelper = new ProgressVideoCellHelper(downloadProgress, cachedModelHelper);
   }

   @Override
   protected void syncUIStateWithModel() {
      ViewUtils.runTaskAfterMeasure(itemView, () -> {
         PipelineDraweeController controller = GraphicUtils
               .provideFrescoResizingController(Uri.parse(getModelObject().getImageUrl()), imageViewPreview.getController(),
                     imageViewPreview.getWidth() , imageViewPreview.getHeight());
         imageViewPreview.setController(controller);
      });
      this.textViewTitle.setText(getModelObject().getVideoName());
      this.textViewDuration.setText(getModelObject().getDuration());

      progressVideoCellHelper.setModelObject(getModelObject().getCacheEntity());
      progressVideoCellHelper.syncUIStateWithModel();
   }

   @OnClick(R.id.iv_bg)
   public void onItemClick() {
      Video video = getModelObject();
      CachedModel cacheEntity = getModelObject().getCacheEntity();
      String url = getModelObject().getVideoUrl();
      if (cachedModelHelper.isCached(cacheEntity)) {
         url = cachedModelHelper.getFilePath(getModelObject().getVideoUrl());
      }
      cellDelegate.onOpen360Video(video, url, video.getVideoName());
   }

   @OnClick(R.id.download_progress)
   public void onDownloadClick() {
      Video video = getModelObject();
      progressVideoCellHelper.onDownloadClick(cellDelegate, video);
   }

   @Override
   public void prepareForReuse() {
      imageViewPreview.setImageResource(0);
   }

   public interface Video360CellDelegate extends VideoCellDelegate {

      void onOpen360Video(Video video, String url, String name);
   }
}

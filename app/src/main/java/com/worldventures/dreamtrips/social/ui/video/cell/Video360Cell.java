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
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.Video360CellDelegate;
import com.worldventures.dreamtrips.social.ui.video.cell.util.ProgressVideoCellHelper;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video_360)
public class Video360Cell extends BaseAbstractDelegateCell<Video, Video360CellDelegate> {

   @InjectView(R.id.textViewDuration) TextView textViewDuration;
   @InjectView(R.id.tv_title) TextView textViewTitle;
   @InjectView(R.id.iv_bg) SimpleDraweeView imageViewPreview;
   @InjectView(R.id.download_progress) PinProgressButton downloadProgress;

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
         final PipelineDraweeController controller = GraphicUtils.provideFrescoResizingController(getModelObject().getImageUrl(),
               imageViewPreview.getController(), imageViewPreview.getWidth(), imageViewPreview.getHeight());
         imageViewPreview.setController(controller);
      });
      this.textViewTitle.setText(getModelObject().getVideoName());
      this.textViewDuration.setText(getModelObject().getDuration());

      progressVideoCellHelper.setModelObject(getModelObject().getCacheEntity());
      progressVideoCellHelper.updateButtonState();
   }

   @OnClick(R.id.iv_bg)
   public void onItemClick() {
      cellDelegate.onOpen360Video(getModelObject());
   }

   @OnClick(R.id.download_progress)
   void onDownloadClick() {
      progressVideoCellHelper.onDownloadClick(cellDelegate, getModelObject());
   }

   @Override
   public void prepareForReuse() {
      imageViewPreview.setController(null);
   }
}

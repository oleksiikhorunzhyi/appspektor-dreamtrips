package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video)
public class VideoCell extends AbstractDelegateCell<Video, VideoCellDelegate> {

   @InjectView(R.id.iv_bg) protected SimpleDraweeView ivBg;
   @InjectView(R.id.tv_title) protected TextView tvTitle;
   @InjectView(R.id.download_progress) protected PinProgressButton downloadProgress;

   @Inject @ForActivity protected Context context;
   @Inject protected SessionHolder appSessionHolder;
   @Inject CachedModelHelper cachedModelHelper;

   protected ProgressVideoCellHelper progressVideoCellHelper;

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
      ivBg.setImageURI(Uri.parse(getModelObject().getImageUrl()));
      tvTitle.setText(getModelObject().getVideoName());

      progressVideoCellHelper.setModelObject(getModelObject().getCacheEntity());

      progressVideoCellHelper.syncUIStateWithModel();
   }

   @OnClick(R.id.iv_play)
   public void onPlayClick() {
      Video video = getModelObject();

      TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, appSessionHolder.get()
            .get()
            .getUser()
            .getUsername(), TrackingHelper.ACTION_MEMBERSHIP_PLAY, video.getVideoName());

      if (cellDelegate != null) {
         cellDelegate.sendAnalytic(TrackingHelper.ATTRIBUTE_VIEW, video.getVideoName());
         cellDelegate.onPlayVideoClicked(video);
      }
   }

   @OnClick(R.id.download_progress)
   public void onDownloadClick() {
      Video video = getModelObject();
      progressVideoCellHelper.onDownloadClick(cellDelegate);
      //
      TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, appSessionHolder.get()
            .get()
            .getUser()
            .getUsername(), TrackingHelper.ACTION_MEMBERSHIP_LOAD_START, video.getVideoName());
      if (cellDelegate != null) cellDelegate.sendAnalytic(TrackingHelper.ATTRIBUTE_DOWNLOAD, video.getVideoName());
   }

   @Override
   public void prepareForReuse() {
      ivBg.setImageResource(0);
   }

}

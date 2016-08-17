package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video_360)
public class Video360Cell extends AbstractDelegateCell<Video, VideoCellDelegate> {

   @InjectView(R.id.textViewDuration) TextView textViewDuration;
   @InjectView(R.id.tv_title) TextView textViewTitle;
   @InjectView(R.id.iv_bg) SimpleDraweeView imageViewPreview;
   @InjectView(R.id.download_progress) PinProgressButton downloadProgress;

   @Inject Context context;
   @Inject ActivityRouter activityRouter;
   @Inject SessionHolder<UserSession> appSessionHolder;

   private ProgressVideoCellHelper progressVideoCellHelper;

   public Video360Cell(View view) {
      super(view);
      progressVideoCellHelper = new ProgressVideoCellHelper(downloadProgress);
   }

   @Override
   protected void syncUIStateWithModel() {
      imageViewPreview.setImageURI(Uri.parse(getModelObject().getImageUrl()));
      this.textViewTitle.setText(getModelObject().getVideoName());
      this.textViewDuration.setText(getModelObject().getDuration());

      progressVideoCellHelper.setModelObject(getModelObject().getCacheEntity());
      progressVideoCellHelper.syncUIStateWithModel();
   }

   @OnClick(R.id.iv_bg)
   public void onItemClick() {
      Video video = getModelObject();
      CachedEntity cacheEntity = getModelObject().getCacheEntity();
      String url = getModelObject().getMp4Url();
      if (cacheEntity.isCached(context)) {
         url = CachedEntity.getFilePath(context, getModelObject().getMp4Url());
      }
      activityRouter.open360Activity(url, video.getVideoName());
      //
      TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, appSessionHolder.get()
            .get()
            .getUser()
            .getUsername(), TrackingHelper.ACTION_360_PLAY, video.getVideoName());
      TrackingHelper.actionTripVideo(TrackingHelper.ATTRIBUTE_VIEW, video.getVideoName());
   }


   @OnClick(R.id.download_progress)
   public void onDownloadClick() {
      Video video = getModelObject();
      progressVideoCellHelper.onDownloadClick(context, cellDelegate);
      //
      TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, appSessionHolder.get()
            .get()
            .getUser()
            .getUsername(), TrackingHelper.ACTION_360_LOAD_START, video.getVideoName());
      TrackingHelper.actionTripVideo(TrackingHelper.ATTRIBUTE_DOWNLOAD, video.getVideoName());
   }

   @Override
   public void prepareForReuse() {
      imageViewPreview.setImageResource(0);
   }
}

package com.worldventures.dreamtrips.social.ui.video.cell;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.VideoCellDelegate;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video)
public class VideoCell extends BaseAbstractDelegateCell<Video, VideoCellDelegate> {

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
      if (cellDelegate != null) {
         cellDelegate.onPlayVideoClicked(video);
      }
   }

   @OnClick(R.id.download_progress)
   public void onDownloadClick() {
      progressVideoCellHelper.onDownloadClick(cellDelegate, getModelObject());
   }

   @Override
   public void prepareForReuse() {
      ivBg.setImageResource(0);
   }

}

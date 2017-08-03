package com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder;

import android.net.Uri;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.databinding.AdapterItemVideoBinding;
import com.worldventures.dreamtrips.modules.video.cell.ProgressVideoCellHelper;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletVideo;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate.WalletVideoCallback;

public class WalletVideoHolder extends BaseHolder<WalletVideo> {

   private final AdapterItemVideoBinding binding;
   private final WalletVideoCallback videoActionsCallback;
   private final SessionHolder<UserSession> appSessionHolder;

   private ProgressVideoCellHelper progressVideoCellHelper;

   public WalletVideoHolder(AdapterItemVideoBinding binding, CachedModelHelper cachedModelHelper,
         WalletVideoCallback videoActionsCallback, SessionHolder<UserSession> appSessionHolder) {
      super(binding.getRoot());
      this.binding = binding;
      this.videoActionsCallback = videoActionsCallback;
      this.appSessionHolder = appSessionHolder;
      progressVideoCellHelper = new ProgressVideoCellHelper(binding.downloadProgress, cachedModelHelper);
   }

   @Override
   public void setData(final WalletVideo video) {
      binding.ivBg.setImageURI(Uri.parse(video.getImageUrl()));
      binding.tvTitle.setText(video.getVideoName());

      progressVideoCellHelper.setModelObject(video.getCacheEntity());

      progressVideoCellHelper.syncUIStateWithModel();

      binding.ivPlay.setOnClickListener(view -> {
         TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, appSessionHolder.get()
               .get()
               .getUser()
               .getUsername(), TrackingHelper.ACTION_MEMBERSHIP_PLAY, video.getVideoName());

         if (videoActionsCallback != null) {
            videoActionsCallback.sendAnalytic(TrackingHelper.ATTRIBUTE_VIEW, video.getVideoName());
            videoActionsCallback.onPlayVideoClicked(video);
         }
      });

      binding.downloadProgress.setOnClickListener(view -> {
         progressVideoCellHelper.onDownloadClick(videoActionsCallback);
         //
         TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, appSessionHolder.get()
               .get()
               .getUser()
               .getUsername(), TrackingHelper.ACTION_MEMBERSHIP_LOAD_START, video.getVideoName());
         if (videoActionsCallback != null) videoActionsCallback.sendAnalytic(TrackingHelper.ATTRIBUTE_DOWNLOAD, video.getVideoName());
      });
   }
}

package com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder;

import android.net.Uri;

import com.worldventures.dreamtrips.databinding.AdapterItemVideoBinding;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate.WalletVideoCallback;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate.WalletVideoHolderHelper;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

public class WalletVideoHolder extends BaseHolder<WalletVideoModel> {

   private final AdapterItemVideoBinding binding;
   private final WalletVideoCallback videoActionsCallback;
   private final WalletVideoHolderDelegate videoHolderDelegate;
   private final WalletVideoHolderHelper videoHolderHelper;

   public WalletVideoHolder(AdapterItemVideoBinding binding,
         WalletVideoCallback videoActionsCallback, WalletVideoHolderDelegate videoHolderDelegate) {
      super(binding.getRoot());
      this.binding = binding;
      this.videoActionsCallback = videoActionsCallback;
      this.videoHolderDelegate = videoHolderDelegate;
      this.videoHolderHelper = videoHolderDelegate.createHelper(binding.downloadProgress);
   }

   @Override
   public void setData(final WalletVideoModel videoModel) {
      final Video video = videoModel.getVideo();
      binding.ivBg.setImageURI(Uri.parse(video.getImageUrl()));
      binding.tvTitle.setText(video.getVideoName());

      videoHolderHelper.setModelObject(videoModel);
      videoHolderHelper.syncUIStateWithModel();

      binding.ivPlay.setOnClickListener(view -> {
         videoHolderDelegate.sendPlayVideoAction(videoModel);
         if (videoActionsCallback != null) {
            videoActionsCallback.onPlayVideoClicked(videoModel);
         }
      });

      binding.downloadProgress.setOnClickListener(view -> {
         videoHolderHelper.onDownloadClick(videoActionsCallback, videoModel);
         videoHolderDelegate.sendMembershipVideoAction(videoModel);
      });
   }
}

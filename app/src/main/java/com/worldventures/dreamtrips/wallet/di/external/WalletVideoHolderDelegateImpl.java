package com.worldventures.dreamtrips.wallet.di.external;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.video.cell.ProgressVideoCellHelper;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate.WalletVideoCallback;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

class WalletVideoHolderDelegateImpl extends WalletVideoHolderDelegate {

   private final SessionHolder<UserSession> appSessionHolder;
   private final CachedModelHelper cachedModelHelper;

   WalletVideoHolderDelegateImpl(SessionHolder<UserSession> appSessionHolder, CachedModelHelper cachedModelHelper) {
      this.appSessionHolder = appSessionHolder;
      this.cachedModelHelper = cachedModelHelper;
   }

   @Override
   public void sendPlayVideoAction(WalletVideoModel video) {
      TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, appSessionHolder.get()
            .get()
            .getUser()
            .getUsername(), TrackingHelper.ACTION_MEMBERSHIP_PLAY, video.getVideo().getVideoName());
   }

   @Override
   public void sendMembershipVideoAction(WalletVideoModel video) {
      TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, appSessionHolder.get()
            .get()
            .getUser()
            .getUsername(), TrackingHelper.ACTION_MEMBERSHIP_LOAD_START, video.getVideo().getVideoName());
   }

   @Override
   public WalletVideoHolderHelper createHelper(PinProgressButton progressView) {
      return new WalletVideoHolderHelperImpl(new ProgressVideoCellHelper(progressView, cachedModelHelper));
   }

   private static class WalletVideoHolderHelperImpl implements WalletVideoHolderHelper {

      private final ProgressVideoCellHelper progressVideoCellHelper;

      private WalletVideoHolderHelperImpl(ProgressVideoCellHelper progressVideoCellHelper) {
         this.progressVideoCellHelper = progressVideoCellHelper;
      }

      @Override
      public void onDownloadClick(WalletVideoCallback videoCallback) {
         progressVideoCellHelper.onDownloadClick(videoCallback);
      }

      @Override
      public void setModelObject(WalletVideoModel walletVideoModel) {
         progressVideoCellHelper.setModelObject(walletVideoModel.getVideo().getCacheEntity());
      }

      @Override
      public void syncUIStateWithModel() {
         progressVideoCellHelper.syncUIStateWithModel();
      }
   }
}

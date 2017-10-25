package com.worldventures.dreamtrips.wallet.di.external;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.ui.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.social.ui.video.cell.ProgressVideoCellHelper;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate.WalletVideoCallback;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

class WalletVideoHolderDelegateImpl extends WalletVideoHolderDelegate {

   private final SessionHolder appSessionHolder;
   private final CachedModelHelper cachedModelHelper;

   WalletVideoHolderDelegateImpl(SessionHolder appSessionHolder, CachedModelHelper cachedModelHelper) {
      this.appSessionHolder = appSessionHolder;
      this.cachedModelHelper = cachedModelHelper;
   }

   @Override
   public void sendPlayVideoAction(WalletVideoModel video) {
   }

   @Override
   public void sendMembershipVideoAction(WalletVideoModel video) {
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
      public void onDownloadClick(WalletVideoCallback videoCallback, WalletVideoModel walletVideoModel) {
         progressVideoCellHelper.onDownloadClick(videoCallback, walletVideoModel);
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

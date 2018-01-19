package com.worldventures.dreamtrips.wallet;

import android.support.annotation.NonNull;

import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.ui.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.social.ui.video.cell.ProgressVideoCellHelper;
import com.worldventures.wallet.ui.settings.help.video.delegate.WalletVideoCallback;
import com.worldventures.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate;
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel;

class WalletVideoHolderDelegateImpl extends WalletVideoHolderDelegate {

   private final CachedModelHelper cachedModelHelper;

   WalletVideoHolderDelegateImpl(CachedModelHelper cachedModelHelper) {
      this.cachedModelHelper = cachedModelHelper;
   }

   @Override
   public void sendPlayVideoAction(@NonNull WalletVideoModel video) {
      // do nothing
   }

   @Override
   public void sendMembershipVideoAction(@NonNull WalletVideoModel video) {
      // do nothing
   }

   @NonNull
   @Override
   public WalletVideoHolderHelper createHelper(@NonNull PinProgressButton progressView) {
      return new WalletVideoHolderHelperImpl(new ProgressVideoCellHelper(progressView, cachedModelHelper));
   }

   private final static class WalletVideoHolderHelperImpl implements WalletVideoHolderHelper {

      private final ProgressVideoCellHelper progressVideoCellHelper;

      private WalletVideoHolderHelperImpl(ProgressVideoCellHelper progressVideoCellHelper) {
         this.progressVideoCellHelper = progressVideoCellHelper;
      }

      @Override
      public void onDownloadClick(@NonNull WalletVideoCallback videoCallback, @NonNull WalletVideoModel walletVideoModel) {
         progressVideoCellHelper.onDownloadClick(videoCallback, walletVideoModel);
      }

      @Override
      public void setModelObject(@NonNull WalletVideoModel walletVideoModel) {
         progressVideoCellHelper.setModelObject(walletVideoModel.getVideo().getCacheEntity());
      }

      @Override
      public void syncUIStateWithModel() {
         progressVideoCellHelper.syncUIStateWithModel();
      }
   }
}

package com.worldventures.wallet.ui.settings.security.lostcard;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface LostCardPresenter extends WalletPresenter<LostCardScreen> {

   void goBack();

   void disableTracking();

   void disableTrackingCanceled();

   void onPermissionRationaleClick();

   void prepareTrackingStateSubscriptions();
}

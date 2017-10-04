package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface LostCardPresenter extends WalletPresenter<LostCardScreen> {

   void goBack();

   void disableTracking();

   void disableTrackingCanceled();

   void onPermissionRationaleClick();

   void prepareTrackingStateSubscriptions();
}

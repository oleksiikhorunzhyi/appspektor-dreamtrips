package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface LostCardPresenter extends WalletPresenter<LostCardScreen> {

   void goBack();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retryFetchAddressWithPlaces(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand);

   void trackDirectionsClick();

   void disableTracking();

   void disableTrackingCanceled();

   void onPermissionRationaleClick();

   void onMapPrepared();
}

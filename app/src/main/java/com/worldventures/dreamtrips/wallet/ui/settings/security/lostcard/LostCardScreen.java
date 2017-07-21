package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.wallet.service.location.LocationSettingsService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.model.LostCardPin;

import java.util.Date;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface LostCardScreen extends WalletScreen{

   LocationSettingsService getLocationSettingsService();

   Observable<Boolean> observeTrackingEnable();

   OperationView<FetchAddressWithPlacesCommand> provideOperationView();

   void setVisibleDisabledTrackingView(boolean visible);

   void setVisibleMsgEmptyLastLocation(boolean visible);

   void setVisibleLastConnectionTime(boolean visible);

   void setVisibilityMap(boolean visible);

   void setLastConnectionDate(Date date);

   void setTrackingSwitchStatus(boolean checked);

   void addPin(LostCardPin lostCardPin);

   void addPin(LatLng position);

   void showRationaleForLocation();

   void showDeniedForLocation();

   void showDisableConfirmationDialog();
}

package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard;


import com.google.android.gms.maps.model.LatLng;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.model.LostCardPin;

import java.util.Date;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface MapScreen extends MvpView {

   void addPin(LostCardPin lostCardPin);

   void addPin(LatLng position);

   void setLastConnectionDate(Date date);

   void setVisibleMsgEmptyLastLocation(boolean visible);

   void setVisibleLastConnectionTime(boolean visible);

   OperationView<FetchAddressWithPlacesCommand> provideOperationView();

   <T> LifecycleTransformer<T> bindToLifecycle();
}

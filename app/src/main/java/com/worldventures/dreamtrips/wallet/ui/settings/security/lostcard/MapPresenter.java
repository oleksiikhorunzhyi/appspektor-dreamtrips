package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard;


import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;

public interface MapPresenter {

   void attachView(MapScreen view);

   void detachView(boolean retainInstance);

   void trackDirectionsClick();

   void onMapPrepared();

   void retryFetchAddressWithPlaces(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand);
}

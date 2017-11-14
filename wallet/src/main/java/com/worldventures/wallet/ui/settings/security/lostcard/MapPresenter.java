package com.worldventures.wallet.ui.settings.security.lostcard;


import com.worldventures.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;

public interface MapPresenter {

   void attachView(MapScreen view);

   void detachView(boolean retainInstance);

   void trackDirectionsClick();

   void onMapPrepared();

   void retryFetchAddressWithPlaces(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand);
}

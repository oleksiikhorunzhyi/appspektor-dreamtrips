package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl;


import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.ClickDirectionsAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.MapPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.MapScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.model.ImmutableLostCardPin;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil.getLatestLocation;
import static com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil.toLatLng;

public class MapPresenterImpl extends MvpBasePresenter<MapScreen> implements MapPresenter {

   private final SmartCardLocationInteractor smartCardLocationInteractor;
   private final AnalyticsInteractor analyticsInteractor;

   public MapPresenterImpl(SmartCardLocationInteractor smartCardLocationInteractor, AnalyticsInteractor analyticsInteractor) {
      this.smartCardLocationInteractor = smartCardLocationInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(MapScreen view) {
      super.attachView(view);
      observeWalletLocationCommand();
   }

   private void observeWalletLocationCommand() {
      smartCardLocationInteractor.walletLocationCommandPipe()
            .observeSuccess()
            .compose(getView().bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(walletLocationCommand -> processLastLocation(walletLocationCommand.getResult()),
                  throwable -> Timber.e(throwable, ""));
   }

   private void setupEmptyLocation(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      getView().addPin(toLatLng(fetchAddressWithPlacesCommand.getCoordinates()));
   }

   private void setupLocationAndAddress(WalletCoordinates coordinates, WalletAddress address, List<WalletPlace> places) {
      getView().addPin(ImmutableLostCardPin.builder()
            .position(toLatLng(coordinates))
            .address(address)
            .places(places)
            .build());
   }

   private void fetchLastSmartCardLocation() {
      smartCardLocationInteractor.getLocationPipe()
            .createObservable(new GetLocationCommand())
            .compose(getView().bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<GetLocationCommand>()
                  .onSuccess(getLocationCommand -> {
                     final WalletLocation walletLocation = getLatestLocation(getLocationCommand.getResult());
                     processLastLocation(walletLocation);
                  })
            );
   }

   private void processLastLocation(WalletLocation walletLocation) {
      if (walletLocation == null) {
         toggleLocationContainersVisibility(false);
         return;
      }
      toggleLocationContainersVisibility(true);
      getView().setLastConnectionDate(walletLocation.createdAt());

      fetchAddressWithPlaces(walletLocation.coordinates());
   }

   private void toggleLocationContainersVisibility(boolean locationExists) {
      getView().setVisibleMsgEmptyLastLocation(!locationExists);
      getView().setVisibleLastConnectionTime(locationExists);
   }

   private void fetchAddressWithPlaces(WalletCoordinates coordinates) {
      smartCardLocationInteractor
            .fetchAddressPipe()
            .createObservable(new FetchAddressWithPlacesCommand(coordinates))
            .compose(getView().bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView(), true)
                  .onSuccess(command ->
                        setupLocationAndAddress(coordinates, command.getResult().address, command.getResult().places))
                  .onFail((fetchAddressWithPlacesCommand, throwable) -> setupEmptyLocation(fetchAddressWithPlacesCommand))
                  .create());
   }

   @Override
   public void retryFetchAddressWithPlaces(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      fetchAddressWithPlaces(fetchAddressWithPlacesCommand.getCoordinates());
   }

   @Override
   public void trackDirectionsClick() {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(new ClickDirectionsAnalyticsAction()));
   }

   @Override
   public void onMapPrepared() {
      fetchLastSmartCardLocation();
   }
}

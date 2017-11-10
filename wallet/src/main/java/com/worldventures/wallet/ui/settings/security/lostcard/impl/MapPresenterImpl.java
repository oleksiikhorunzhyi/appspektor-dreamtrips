package com.worldventures.wallet.ui.settings.security.lostcard.impl;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.wallet.analytics.locatecard.action.ClickDirectionsAnalyticsAction;
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.wallet.ui.settings.security.lostcard.MapPresenter;
import com.worldventures.wallet.ui.settings.security.lostcard.MapScreen;
import com.worldventures.wallet.ui.settings.security.lostcard.model.LostCardPin;
import com.worldventures.wallet.util.WalletLocationsUtil;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MapPresenterImpl extends MvpBasePresenter<MapScreen> implements MapPresenter {

   private final SmartCardLocationInteractor smartCardLocationInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public MapPresenterImpl(SmartCardLocationInteractor smartCardLocationInteractor, WalletAnalyticsInteractor analyticsInteractor) {
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
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(walletLocationCommand -> processLastLocation(walletLocationCommand.getResult()),
                  throwable -> Timber.e(throwable, ""));
   }

   private void setupEmptyLocation(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      getView().addPin(WalletLocationsUtil.INSTANCE.toLatLng(fetchAddressWithPlacesCommand.getCoordinates()));
   }

   private void setupLocationAndAddress(WalletCoordinates coordinates, WalletAddress address, List<WalletPlace> places) {
      getView().addPin(new LostCardPin(places, address, WalletLocationsUtil.INSTANCE.toLatLng(coordinates)));
   }

   private void fetchLastSmartCardLocation() {
      smartCardLocationInteractor.getLocationPipe()
            .createObservable(new GetLocationCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<GetLocationCommand>()
                  .onSuccess(getLocationCommand -> {
                     final List<WalletLocation> locations = (List<WalletLocation>) getLocationCommand.getResult();
                     final WalletLocation walletLocation = WalletLocationsUtil.INSTANCE.getLatestLocation(locations);
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
      getView().setLastConnectionDate(walletLocation.getCreatedAt());

      fetchAddressWithPlaces(walletLocation.getCoordinates());
   }

   private void toggleLocationContainersVisibility(boolean locationExists) {
      getView().setVisibleMsgEmptyLastLocation(!locationExists);
   }

   private void fetchAddressWithPlaces(WalletCoordinates coordinates) {
      smartCardLocationInteractor
            .fetchAddressPipe()
            .createObservable(new FetchAddressWithPlacesCommand(coordinates))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView(), true)
                  .onSuccess(command ->
                        setupLocationAndAddress(coordinates, command.getResult().getAddress(), command.getResult()
                              .getPlaces()))
                  .onFail((fetchAddressWithPlacesCommand, throwable) -> setupEmptyLocation(fetchAddressWithPlacesCommand))
                  .create());
   }

   @Override
   public void retryFetchAddressWithPlaces(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      fetchAddressWithPlaces(fetchAddressWithPlacesCommand.getCoordinates());
   }

   @Override
   public void trackDirectionsClick() {
      analyticsInteractor.locateCardAnalyticsPipe()
            .send(new LocateCardAnalyticsCommand(new ClickDirectionsAnalyticsAction()));
   }

   @Override
   public void onMapPrepared() {
      fetchLastSmartCardLocation();
   }
}

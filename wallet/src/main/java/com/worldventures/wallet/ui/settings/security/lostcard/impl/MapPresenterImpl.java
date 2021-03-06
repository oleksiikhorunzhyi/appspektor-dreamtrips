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
            .subscribe(walletLocationCommand -> processLastLocation(walletLocationCommand.getResult()), Timber::e);
   }

   private void setupLocationAndAddress(WalletCoordinates coordinates, WalletAddress address, List<WalletPlace> places) {
      getView().addPin(new LostCardPin(places, address, coordinates));
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
         getView().setCoordinates(null);
         return;
      }
      getView().setLastConnectionDate(walletLocation.getCreatedAt());
      getView().setCoordinates(walletLocation.getCoordinates());

      fetchAddressWithPlaces(walletLocation.getCoordinates());
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
   public void fetchLastKnownLocation() {
      fetchLastSmartCardLocation();
   }
}

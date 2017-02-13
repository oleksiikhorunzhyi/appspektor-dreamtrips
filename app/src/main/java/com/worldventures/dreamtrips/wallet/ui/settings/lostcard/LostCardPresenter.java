package com.worldventures.dreamtrips.wallet.ui.settings.lostcard;

import android.content.Context;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.location.LocationSettingsService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.SaveEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.ImmutableLostCardPin;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil.getLatestLocation;
import static com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil.toLatLng;

public class LostCardPresenter extends WalletPresenter<LostCardPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject PermissionDispatcher permissionDispatcher;
   @Inject SmartCardLocationInteractor smartCardLocationInteractor;

   private final LocationSettingsService locationSettingsService;

   public LostCardPresenter(Context context, LocationSettingsService locationSettingsService, Injector injector) {
      super(context, injector);
      this.locationSettingsService = locationSettingsService;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeSaveSwitcherState();
      fetchEnableTrackingState();
      observeWalletLocationCommand();
   }

   private void observeWalletLocationCommand() {
      smartCardLocationInteractor.walletLocationCommandPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(walletLocationCommand -> processLastLocation(walletLocationCommand.getResult()));
   }

   private void observeSaveSwitcherState() {
      smartCardLocationInteractor.saveEnabledTrackingPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               getView().toggleVisibleDisabledOfTrackingView(!command.getResult());
               getView().setVisibilityMap(command.getResult());
            });
   }

   private void fetchEnableTrackingState() {
      smartCardLocationInteractor.enabledTrackingCommandActionPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               observeEnableSwitcher();
               getView().toggleLostCardSwitcher(command.getResult());
            }, throwable -> Timber.e(throwable, ""));
      smartCardLocationInteractor.enabledTrackingCommandActionPipe().send(new GetEnabledTrackingCommand());
   }

   private void observeEnableSwitcher() {
      getView().observeTrackingEnable()
            .compose(bindView())
            .skip(1)
            .subscribe(this::onTrackingSwitcherChanged);
   }

   private void onTrackingSwitcherChanged(boolean enableTracking) {
      if (enableTracking) {
         requestLocationPermissions(true);
      } else {
         executeToggleTracking(false);
      }
   }

   void requestLocationPermissions(boolean showRationale) {
      permissionDispatcher.requestPermission(PermissionConstants.LOCATION_PERMISSIONS, showRationale)
            .compose(bindView())
            .subscribe(new PermissionSubscriber()
                  .onPermissionGrantedAction(this::checkLocationServiceEnabled)
                  .onPermissionRationaleAction(() -> {
                     getView().showRationaleForLocation();
                     getView().toggleLostCardSwitcher(false);
                  })
                  .onPermissionDeniedAction(() -> {
                     getView().showDeniedForLocation();
                     getView().toggleLostCardSwitcher(false);
                  }));
   }

   void onMapPrepared() {
      fetchLastSmartCardLocation();
   }

   void disableTracking() {
      smartCardLocationInteractor.saveEnabledTrackingPipe().send(new SaveEnabledTrackingCommand(false));
   }

   void disableTrackingCanceled() {
      getView().toggleLostCardSwitcher(false);
   }

   public void goBack() {
      navigator.goBack();
   }

   private void checkLocationServiceEnabled() {
      locationSettingsService.enableLocationApi()
            .compose(bindView())
            .take(1)
            .subscribe(this::onLocationSettingsResult);
   }

   private void onLocationSettingsResult(LocationSettingsService.EnableResult result) {
      switch (result) {
         case AVAILABLE:
            executeToggleTracking(true);
            break;
         default:
            getView().toggleLostCardSwitcher(false);
      }
   }

   private void executeToggleTracking(boolean enableTracking) {
      if (!enableTracking) {
         getView().showDisableConfirmationDialog();
         return;
      }
      smartCardLocationInteractor.saveEnabledTrackingPipe().send(new SaveEnabledTrackingCommand(true));
   }

   private void fetchLastSmartCardLocation() {
      smartCardLocationInteractor.getLocationPipe()
            .createObservable(new GetLocationCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<GetLocationCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(getLocationCommand -> {
                     final WalletLocation walletLocation = getLatestLocation(getLocationCommand.getResult());
                     processLastLocation(walletLocation);
                  })
                  .wrap());
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

   public void fetchAddressWithPlaces(WalletCoordinates coordinates) {
      smartCardLocationInteractor.fetchAddressPipe()
            .createObservable(new FetchAddressWithPlacesCommand(coordinates))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView(), true)
                  .onSuccess(command ->
                        setupLocationAndAddress(coordinates, command.getResult().address, command.getResult().places))
                  .onFail((fetchAddressWithPlacesCommand, throwable) -> setupEmptyLocation(fetchAddressWithPlacesCommand))
                  .create());
   }

   void retryFetchAddressWithPlaces(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      fetchAddressWithPlaces(fetchAddressWithPlacesCommand.getCoordinates());
   }

   void setupEmptyLocation(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      getView().addPin(toLatLng(fetchAddressWithPlacesCommand.getCoordinates()));
   }

   private void toggleLocationContainersVisibility(boolean locationExists) {
      getView().toggleVisibleMsgEmptyLastLocation(!locationExists);
      getView().toggleVisibleLastConnectionTime(locationExists);
   }

   private void setupLocationAndAddress(WalletCoordinates coordinates, WalletAddress address, List<WalletPlace> places) {
      getView().addPin(ImmutableLostCardPin.builder()
            .position(toLatLng(coordinates))
            .address(address)
            .places(places)
            .build());
   }

   public interface Screen extends WalletScreen {

      Observable<Boolean> observeTrackingEnable();

      OperationView<FetchAddressWithPlacesCommand> provideOperationView();

      void toggleVisibleDisabledOfTrackingView(boolean visible);

      void toggleVisibleMsgEmptyLastLocation(boolean visible);

      void toggleVisibleLastConnectionTime(boolean visible);

      void setVisibilityMap(boolean visible);

      void setLastConnectionDate(Date date);

      void toggleLostCardSwitcher(boolean checked);

      void addPin(LostCardPin lostCardPin);

      Marker addPin(LatLng position);

      void showRationaleForLocation();

      void showDeniedForLocation();

      void showDisableConfirmationDialog();
   }
}

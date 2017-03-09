package com.worldventures.dreamtrips.wallet.ui.settings.lostcard;

import android.content.Context;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.DisableTrackingLocateSmartCardAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.DisplayLocateClickDirectionsAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.DisplayLocateSmartCardAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.EnableLocateSmartCardAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.OpenLocateSmartCardAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.location.LocationSettingsService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.CardTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.ImmutableLostCardPin;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
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
   @Inject WalletDetectLocationService locationService;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final LocationSettingsService locationSettingsService;

   public LostCardPresenter(Context context, LocationSettingsService locationSettingsService, Injector injector) {
      super(context, injector);
      this.locationSettingsService = locationSettingsService;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      observeCheckingSwitcher();
      observeWalletLocationCommand();
      observeEnableTrackingState();
      observeLocationSettings();

      fetchEnableTrackingState();
   }

   private void observeLocationSettings() {
      locationService.observeLocationSettingState()
            .compose(bindViewIoToMainComposer())
            .subscribe(this::handleLocationSettingsStatus,
                  throwable -> Timber.e(throwable, ""));
   }

   private void observeWalletLocationCommand() {
      smartCardLocationInteractor.walletLocationCommandPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(walletLocationCommand -> processLastLocation(walletLocationCommand.getResult()),
                  throwable -> Timber.e(throwable, ""));
   }

   private void observeEnableTrackingState() {
      smartCardLocationInteractor.enabledTrackingPipe()
            .observeSuccess()
            .distinctUntilChanged(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> onTrackingStateFetched(command.getResult()));
   }

   private void fetchEnableTrackingState() {
      smartCardLocationInteractor.enabledTrackingPipe().send(CardTrackingStatusCommand.fetch());
   }

   private void handleLocationSettingsStatus(boolean isEnabled) {
      if (!isEnabled) {
         smartCardLocationInteractor.enabledTrackingPipe()
               .createObservable(CardTrackingStatusCommand.fetch())
               .compose(bindViewIoToMainComposer())
               .subscribe(new ActionStateSubscriber<CardTrackingStatusCommand>()
                     .onSuccess(command -> onTrackingStateFetched(command.getResult())));
      }
   }

   private void onTrackingStateFetched(boolean state) {
      if (state) {
         requestLocationPermissions(true);
      }
      applyTrackingStatusForUI(state);
      sendAnalyticsLocateSmartCard(state);
   }

   private void observeCheckingSwitcher() {
      getView().observeTrackingEnable()
            .compose(bindView())
            .subscribe(this::onTrackingSwitcherChanged);
   }

   private void onTrackingSwitcherChanged(boolean enableTracking) {
      sendAnalyticsSwitchStateChanged(enableTracking);
      if (enableTracking) {
         applyTrackingStatus(true);
      } else {
         getView().showDisableConfirmationDialog();
      }
   }

   private void requestLocationPermissions(boolean showRationale) {
      permissionDispatcher.requestPermission(PermissionConstants.LOCATION_PERMISSIONS, showRationale)
            .compose(bindView())
            .subscribe(new PermissionSubscriber()
                  .onPermissionGrantedAction(this::checkLocationServiceEnabled)
                  .onPermissionRationaleAction(() -> {
                     getView().showRationaleForLocation();
                     applyTrackingStatus(false);
                  })
                  .onPermissionDeniedAction(() -> {
                     getView().showDeniedForLocation();
                     applyTrackingStatus(false);
                  }));
   }

   private void applyTrackingStatusForUI(boolean isTrackingEnabled) {
      getView().setTrackingSwitchStatus(isTrackingEnabled);

      getView().setVisibleDisabledTrackingView(!isTrackingEnabled);
      getView().setVisibilityMap(isTrackingEnabled);
   }

   void onMapPrepared() {
      fetchLastSmartCardLocation();
   }

   void disableTracking() {
      applyTrackingStatus(false);
   }

   void onPermissionRationaleClick() {
      requestLocationPermissions(false);
   }

   void disableTrackingCanceled() {
      applyTrackingStatusForUI(true);
   }

   public void goBack() {
      navigator.goBack();
   }

   void retryFetchAddressWithPlaces(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      fetchAddressWithPlaces(fetchAddressWithPlacesCommand.getCoordinates());
   }

   private void checkLocationServiceEnabled() {
      locationSettingsService.enableLocationApi()
            .compose(bindView())
            .take(1)
            .subscribe(this::onLocationSettingsResult);
   }

   private void onLocationSettingsResult(LocationSettingsService.EnableResult result) {
      sendAnalyticsForGpsLocationStateChanged();
      applyTrackingStatus(result == LocationSettingsService.EnableResult.AVAILABLE);
   }

   private void applyTrackingStatus(boolean enableTracking) {
      smartCardLocationInteractor.enabledTrackingPipe().send(CardTrackingStatusCommand.save(enableTracking));
   }


   private void fetchLastSmartCardLocation() {
      smartCardLocationInteractor.getLocationPipe()
            .createObservable(new GetLocationCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<GetLocationCommand>()
                  .onSuccess(getLocationCommand -> {
                     final WalletLocation walletLocation = getLatestLocation(getLocationCommand.getResult());
                     sendAnalyticsLastLocation(walletLocation);
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

   private void fetchAddressWithPlaces(WalletCoordinates coordinates) {
      smartCardLocationInteractor.fetchAddressPipe()
            .createObservable(new FetchAddressWithPlacesCommand(coordinates))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView(), true)
                  .onSuccess(command ->
                        setupLocationAndAddress(coordinates, command.getResult().address, command.getResult().places))
                  .onFail((fetchAddressWithPlacesCommand, throwable) -> setupEmptyLocation(fetchAddressWithPlacesCommand))
                  .create());
   }

   private void setupEmptyLocation(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      getView().addPin(toLatLng(fetchAddressWithPlacesCommand.getCoordinates()));
   }

   private void toggleLocationContainersVisibility(boolean locationExists) {
      getView().setVisibleMsgEmptyLastLocation(!locationExists);
      getView().setVisibleLastConnectionTime(locationExists);
   }

   private void setupLocationAndAddress(WalletCoordinates coordinates, WalletAddress address, List<WalletPlace> places) {
      getView().addPin(ImmutableLostCardPin.builder()
            .position(toLatLng(coordinates))
            .address(address)
            .places(places)
            .build());
   }

   private void sendAnalyticsLocateSmartCard(boolean state) {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(OpenLocateSmartCardAction.forLocateSmartCard(state)));
   }

   private void sendAnalyticsLastLocation(WalletLocation walletLocation) {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(DisplayLocateSmartCardAction
                  .forLocateSmartCard(walletLocation != null)));
   }

   void sendAnalyticsClickDirections() {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(new DisplayLocateClickDirectionsAction()));
   }

   private void sendAnalyticsForGpsLocationStateChanged() {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(new EnableLocateSmartCardAction()));
   }

   private void sendAnalyticsSwitchStateChanged(boolean enableTracking) {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(enableTracking
                  ? new EnableLocateSmartCardAction() : new DisableTrackingLocateSmartCardAction()));
   }

   public interface Screen extends WalletScreen {

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
}

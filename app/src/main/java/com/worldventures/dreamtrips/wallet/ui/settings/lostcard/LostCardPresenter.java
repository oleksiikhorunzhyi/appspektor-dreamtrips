package com.worldventures.dreamtrips.wallet.ui.settings.lostcard;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.location.LocationServiceDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.wallet.service.command.FetchPlacesNearbyCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.model.LocationPlace;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.SaveEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.ImmutableLostCardPin;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;
import com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class LostCardPresenter extends WalletPresenter<LostCardPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject PermissionDispatcher permissionDispatcher;
   @Inject LocationServiceDispatcher locationServiceDispatcher;
   @Inject SmartCardLocationInteractor smartCardLocationInteractor;

   private SimpleDateFormat lastConnectedDateFormat =
         new SimpleDateFormat("EEEE, MMMM dd, h:mma", Locale.US);

   public LostCardPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeSaveSwitcherState();
      getEnableTrackingState();
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

   private void getEnableTrackingState() {
      smartCardLocationInteractor.enabledTrackingCommandActionPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               observeEnableSwitcher(getView());
               getView().toggleLostCardSwitcher(command.getResult());
            }, throwable -> Timber.e(throwable, ""));
      smartCardLocationInteractor.enabledTrackingCommandActionPipe().send(new GetEnabledTrackingCommand());
   }

   private void observeEnableSwitcher(Screen view) {
      view.observeTrackingEnable()
            .compose(bindView())
            .skip(1)
            .subscribe(this::enableToggleTracking);
   }

   private void enableToggleTracking(boolean enableTracking) {
      requestPermissions(enableTracking);
   }

   public void requestPermissions(boolean enableTracking) {
      permissionDispatcher.requestPermission(PermissionConstants.LOCATION_PERMISSIONS)
            .compose(bindViewIoToMainComposer())
            .subscribe(new PermissionSubscriber()
                  .onPermissionGrantedAction(() -> checkLocationServiceEnabled(enableTracking))
                  .onPermissionRationaleAction(() -> getView().showRationaleForLocation())
                  .onPermissionDeniedAction(() -> getView().showDeniedForLocation()));
   }

   private void checkLocationServiceEnabled(boolean enableTracking) {
      locationServiceDispatcher.checkEnableLocationService()
            .compose(bindViewIoToMainComposer())
            .subscribe(isEnableLocationServices -> {
               if (isEnableLocationServices) {
                  executeToggleTracking(enableTracking);
               } else {
                  getView().showOpenLocationServiceSettingsDialog();
                  getView().toggleLostCardSwitcher(false);
               }
            });
   }

   private void executeToggleTracking(boolean enableTracking) {
      getView().onTrackingChecked(enableTracking);
      if (!getView().showDisableConfirmationDialogIfNeed(enableTracking)) {
         smartCardLocationInteractor.saveEnabledTrackingPipe().send(new SaveEnabledTrackingCommand(enableTracking));
      }
   }

   public void loadLastSmartCardLocation() {
      smartCardLocationInteractor.getLocationPipe()
            .createObservable(new GetLocationCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<GetLocationCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(getLocationCommand -> {
                     final WalletLocation walletLocation = takeLastLocation(getLocationCommand.getResult());
                     processLastLocation(walletLocation);
                  })
                  .onFail(throwable -> {
                     Timber.e(throwable, "");
                     return null;
                  })
                  .wrap());
   }

   private WalletLocation takeLastLocation(List<WalletLocation> walletLocations) {
      return !walletLocations.isEmpty() ? WalletLocationsUtil.getLatestLocation(walletLocations) : null;
   }

   private void processLastLocation(WalletLocation walletLocation) {
      if (walletLocation == null) {
         toggleLocationContainersVisibility(false);
         return;
      }
      toggleLocationContainersVisibility(true);
      getView().setLastConnectionLabel(lastConnectedDateFormat.format(walletLocation.createdAt()));

      smartCardLocationInteractor.fetchAddressPipe()
            .createObservable(new FetchAddressCommand(walletLocation.coordinates()))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FetchAddressCommand>()
                  .onSuccess(command -> setupLocationAndAddress(walletLocation.coordinates(), command.getResult()))
                  .onFail((fetchAddressCommand, throwable) -> {
                     Timber.e(throwable, "");
                     toggleLocationContainersVisibility(false);
                  })
            );
   }

   private void toggleLocationContainersVisibility(boolean locationExists) {
      getView().toggleVisibleMsgEmptyLastLocation(!locationExists);
      getView().toggleVisibleLastConnectionTime(locationExists);
   }

   private void setupLocationAndAddress(@NonNull WalletCoordinates walletLocation, @NonNull WalletAddress address) {
      LostCardPin lostCardPin = ImmutableLostCardPin.builder()
            .address(String.format("%s, %s\n%s", address.countryName(), address.adminArea(), address.addressLine()))
            .position(walletLocation)
            .build();

      loadPlace(lostCardPin, walletLocation);
   }

   private void loadPlace(LostCardPin lostCardPin, WalletCoordinates position) {
      smartCardLocationInteractor.fetchPlacesNearbyCommandActionPipe()
            .createObservable(new FetchPlacesNearbyCommand(position))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FetchPlacesNearbyCommand>()
                  .onSuccess(command -> getView().addPin(createLostCardPinWithLastLocation(lostCardPin, command.getResult())))
                  .onFail((command, throwable) -> {
                     getView().addPin(lostCardPin);
                     Timber.e(throwable, "");
                  })
            );
   }

   private LostCardPin createLostCardPinWithLastLocation(LostCardPin lostCardPin, List<LocationPlace> locationPlaces) {
      if (locationPlaces == null || locationPlaces.isEmpty() || locationPlaces.size() > 1) {
         return lostCardPin;
      } else {
         return ImmutableLostCardPin.copyOf(lostCardPin)
               .withPlace(locationPlaces.get(0).name());
      }
   }

   public void disableTracking() {
      smartCardLocationInteractor.saveEnabledTrackingPipe().send(new SaveEnabledTrackingCommand(false));
   }

   public void requestLocationSettings() {
      locationServiceDispatcher.requestLocationSettings();
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      Observable<Boolean> observeTrackingEnable();

      void toggleVisibleDisabledOfTrackingView(boolean visible);

      void toggleVisibleMsgEmptyLastLocation(boolean visible);

      void toggleVisibleLastConnectionTime(boolean visible);

      void setVisibilityMap(boolean visible);

      void setLastConnectionLabel(String lastConnection);

      void toggleLostCardSwitcher(boolean checked);

      void addPin(@NonNull LostCardPin lostCardPin);

      void onTrackingChecked(boolean checked);

      void showRationaleForLocation();

      void showDeniedForLocation();

      void showOpenLocationServiceSettingsDialog();

      boolean showDisableConfirmationDialogIfNeed(boolean enableTracking);
   }
}

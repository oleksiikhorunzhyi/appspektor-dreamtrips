package com.worldventures.dreamtrips.wallet.ui.settings.lostcard;

import android.content.Context;
import android.location.Address;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.location.LocationServiceDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.SaveEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.ImmutableLostCardPin;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class LostCardPresenter extends WalletPresenter<LostCardPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
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
      getEnableTrackingState();
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
      locationServiceDispatcher.requestEnableLocationService()
            .compose(bindViewIoToMainComposer())
            .subscribe(isEnableLocationServices -> {
               if (isEnableLocationServices) {
                  executeToggleTracking(enableTracking);
               } else {
                  getView().toggleLostCardSwitcher(false);
               }
            });
   }

   private void executeToggleTracking(boolean enableTracking) {
      smartCardLocationInteractor.saveEnabledTrackingPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               getView().toggleVisibleDisabledOfTrackingView(!enableTracking);
               getView().onTrackingChecked(enableTracking);
               getView().setVisibilityMap(enableTracking);
            });
      smartCardLocationInteractor.saveEnabledTrackingPipe().send(new SaveEnabledTrackingCommand(enableTracking));
   }

   public void loadLastSmartCardLocation() {
      smartCardLocationInteractor.getLocationPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<GetLocationCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(getLocationCommand -> takeLastLocationAndShow(getLocationCommand.getResult()))
                  .onFail(throwable -> {
                     Timber.e(throwable, "");
                     getView().toggleVisibleMsgEmptyLastLocation(true);
                     getView().toggleVisibleLastConnectionTime(false);
                     return null;
                  })
                  .wrap());
      smartCardLocationInteractor.getLocationPipe().send(new GetLocationCommand());
   }

   private void takeLastLocationAndShow(List<WalletLocation> walletLocations) {
      if (walletLocations == null || walletLocations.isEmpty()) {
         getView().toggleVisibleMsgEmptyLastLocation(true);
         getView().toggleVisibleLastConnectionTime(false);
      } else {
         final WalletLocation walletLocation = WalletLocationsUtil.getLatestLocation(walletLocations);
         smartCardLocationInteractor.fetchAddressPipe()
               .observe()
               .compose(bindViewIoToMainComposer())
               .subscribe(new ActionStateSubscriber<FetchAddressCommand>()
                     .onSuccess(command -> setupLocationAndAddress(walletLocation, command.getResult()))
                     .onFail((fetchAddressCommand, throwable) -> Timber.e(throwable, ""))
               );
         smartCardLocationInteractor.fetchAddressPipe().send(
               new FetchAddressCommand(
                     walletLocation.coordinates().lat(),
                     walletLocation.coordinates().lng())
         );
      }
   }

   private void setupLocationAndAddress(@NonNull WalletLocation walletLocation, @NonNull Address address) {
      LostCardPin lostCardPin = ImmutableLostCardPin.builder()
            .address(String.format("%s\n%s, %s",
                  address.getAddressLine(0), address.getCountryName(), address.getSubAdminArea() + address.getPostalCode()))
            .place(address.getAdminArea())
            .position(new LatLng(
                  walletLocation.coordinates().lat(),
                  walletLocation.coordinates().lng()))
            .build();

      getView().toggleVisibleMsgEmptyLastLocation(false);
      getView().toggleVisibleLastConnectionTime(true);
      getView().setLastConnectionLabel(lastConnectedDateFormat.format(walletLocation.createdAt()));
      getView().addPin(lostCardPin);
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
   }
}

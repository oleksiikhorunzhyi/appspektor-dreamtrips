package com.worldventures.dreamtrips.wallet.ui.settings.lostcard;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import javax.inject.Inject;

import rx.Observable;

public class LostCardPresenter extends WalletPresenter<LostCardPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject Navigator navigator;
   @Inject PermissionDispatcher permissionDispatcher;

   public LostCardPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeEnableSwitcher(view);
   }

   private void observeEnableSwitcher(Screen view) {
      view.observeTrackingEnable()
            .compose(bindView())
            .skip(1)
            .subscribe(this::enableToggleTracking);
   }

   private void enableToggleTracking(boolean enableTracking) {
      requestPermissions(enableTracking);
      // TODO: 2/2/17 SAVE state of enabled tracking
   }

   public void requestPermissions(boolean enableTracking) {
      permissionDispatcher.requestPermission(PermissionConstants.LOCATION_PERMISSIONS)
            .subscribe(new PermissionSubscriber()
                  .onPermissionGrantedAction(() -> executeToggleTracking(enableTracking))
                  .onPermissionRationaleAction(() -> getView().showRationaleForLocation())
                  .onPermissionDeniedAction(() -> getView().showDeniedForLocation()));
   }

   private void executeToggleTracking(boolean enableTracking) {
      getView().onTrackingChecked(enableTracking);
      getView().toggleVisibleDisabledOfTrackingView(!enableTracking);
      // TODO: 2/1/17 add services toggle enable tracking of SC location
   }

   public void loadLastSmartCardLocation() {
      // TODO: 2/1/17 Add logic for request last location smartcard
      // TODO: after load need to call getView().addPin(<position data>)
      // TODO: if data empty call getView().toggleVisibleMsgEmptyLastLocation(true)
      // TODO: if has data call getView().toggleVisibleMsgEmptyLastLocation(false),
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      Observable<Boolean> observeTrackingEnable();

      void toggleVisibleDisabledOfTrackingView(boolean visible);

      void toggleVisibleMsgEmptyLastLocation(boolean visible);

      void toggleVisibleLastConnectionTime(boolean visible);

      void toggleVisibleMap(boolean visible);

      void setLastConnectionLabel(String lastConnection);

      void toggleSwitcher(boolean checked);

      void addPin(LostCardPin lostCardPin);

      void onTrackingChecked(boolean checked);

      void showRationaleForLocation();

      void showDeniedForLocation();
   }
}

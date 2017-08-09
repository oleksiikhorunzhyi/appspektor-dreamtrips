package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl;


import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.custom.ControllerFlipListener;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.custom.LostCardControllerFlipper;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.custom.LostCardControllerFlipperImpl;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Observable;

public class LostCardScreenImpl extends WalletBaseController<LostCardScreen, LostCardPresenter> implements LostCardScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tracking_enable_switcher) WalletSwitcher trackingEnableSwitcher;
   @InjectView(R.id.map_container) FrameLayout mapContainer;

   @Inject LostCardPresenter presenter;

   private final LostCardControllerFlipper controllerFlipper;

   private Observable<Boolean> enableTrackingObservable;

   public LostCardScreenImpl() {
      this.controllerFlipper = new LostCardControllerFlipperImpl();
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      enableTrackingObservable = RxCompoundButton.checkedChanges(trackingEnableSwitcher).skip(1);
      controllerFlipper.init(getChildRouter(mapContainer), new ControllerFlipListener() {
         @Override
         public void onFlipStarted() {
            trackingEnableSwitcher.setEnabled(false);
         }

         @Override
         public void onFlipEnded() {
            trackingEnableSwitcher.setEnabled(true);
         }
      });
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public Observable<Boolean> observeTrackingEnable() {
      return enableTrackingObservable;
   }

   @Override
   public void setMapEnabled(boolean enabled) {
      controllerFlipper.flip(enabled);
   }

   @Override
   public void setTrackingSwitchStatus(boolean checked) {
      trackingEnableSwitcher.setCheckedWithoutNotify(checked);
   }

   @Override
   public void showDisableConfirmationDialog() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_disable_tracking_msg)
            .positiveText(R.string.wallet_disable_tracking)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> getPresenter().disableTracking())
            .onNegative((dialog, which) -> getPresenter().disableTrackingCanceled())
            .cancelListener(dialog -> getPresenter().disableTrackingCanceled())
            .build()
            .show();
   }

   @Override
   public void showRationaleForLocation() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_location_permission_message)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> getPresenter().onPermissionRationaleClick())
            .build()
            .show();
   }

   @Override
   public void showDeniedForLocation() {
      Snackbar.make(getView(), R.string.wallet_lost_card_no_permission, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(getView());
   }

   @Override
   public LostCardPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_lost_card, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }

   @Override
   protected void onChangeEnded(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
      super.onChangeEnded(changeHandler, changeType);
      if (changeType == ControllerChangeType.PUSH_ENTER || changeType == ControllerChangeType.POP_ENTER) {
         getPresenter().prepareTrackingStateSubscriptions();
      }
   }

   @Override
   protected void onDestroyView(@NonNull View view) {
      controllerFlipper.destroy();
      super.onDestroyView(view);
   }
}

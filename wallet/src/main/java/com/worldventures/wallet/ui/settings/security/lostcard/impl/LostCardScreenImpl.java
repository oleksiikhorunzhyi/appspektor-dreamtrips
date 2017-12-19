package com.worldventures.wallet.ui.settings.security.lostcard.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.R;
import com.worldventures.wallet.service.lostcard.command.UpdateTrackingStatusCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.wallet.ui.settings.security.lostcard.LostCardScreen;
import com.worldventures.wallet.ui.settings.security.lostcard.custom.ControllerFlipListener;
import com.worldventures.wallet.ui.settings.security.lostcard.custom.LostCardControllerFlipper;
import com.worldventures.wallet.ui.settings.security.lostcard.custom.LostCardControllerFlipperImpl;
import com.worldventures.wallet.ui.widget.WalletSwitcher;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class LostCardScreenImpl extends WalletBaseController<LostCardScreen, LostCardPresenter> implements LostCardScreen {

   private WalletSwitcher trackingEnableSwitcher;

   @Inject LostCardPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private final LostCardControllerFlipper controllerFlipper = new LostCardControllerFlipperImpl();

   private Observable<Boolean> enableTrackingObservable;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      trackingEnableSwitcher = view.findViewById(R.id.tracking_enable_switcher);
      enableTrackingObservable = RxCompoundButton.checkedChanges(trackingEnableSwitcher).skip(1);
      final FrameLayout mapContainer = view.findViewById(R.id.map_container);
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
   public void switcherEnable(boolean enable) {
      trackingEnableSwitcher.setEnabled(enable);
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
            .negativeText(R.string.wallet_cancel_label)
            .onPositive((dialog, which) -> getPresenter().disableTracking())
            .onNegative((dialog, which) -> getPresenter().disableTrackingCanceled())
            .cancelListener(dialog -> getPresenter().disableTrackingCanceled())
            .build()
            .show();
   }

   @Override
   public OperationView<UpdateTrackingStatusCommand> provideOperationUpdateTrackingStatus() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_update_tracking_status, false),
            ErrorViewFactory.<UpdateTrackingStatusCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        command -> { /*nothing*/ }, command -> { /*nothing*/ }))
                  .build()
      );
   }

   @Override
   public void revertTrackingSwitch() {
      trackingEnableSwitcher.setCheckedWithoutNotify(!trackingEnableSwitcher.isChecked());
   }

   @Override
   public void showRationaleForLocation() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_location_permission_message)
            .positiveText(R.string.wallet_ok)
            .negativeText(R.string.wallet_cancel_label)
            .onPositive((dialog, which) -> getPresenter().onPermissionRationaleClick())
            .build()
            .show();
   }

   @Override
   public void showDeniedForLocation() {
      Snackbar.make(getView(), R.string.wallet_lost_card_no_permission, Snackbar.LENGTH_SHORT).show();
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
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      getPresenter().prepareTrackingStateSubscriptions();
   }

   @Override
   protected void onDestroyView(@NonNull View view) {
      controllerFlipper.destroy();
      super.onDestroyView(view);
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new LostCardScreenModule();
   }
}

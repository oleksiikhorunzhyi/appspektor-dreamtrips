package com.worldventures.dreamtrips.wallet.ui.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.AutoClearSmartCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.DisableDefaultCardItemProvider;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class WalletCardSettingsScreen extends WalletFrameLayout<WalletCardSettingsPresenter.Screen, WalletCardSettingsPresenter, WalletCardSettingsPath> implements WalletCardSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.stealth_mode_switcher) SwitchCompat stealthModeSwitcher;
   @InjectView(R.id.lock_switcher) SwitchCompat lockSwitcher;
   @InjectView(R.id.test_connection_switcher) SwitchCompat testConnectionSwitcher;

   @InjectView(R.id.disable_default_payment_card_label) TextView disableDefaultPaymentCardAfterLabel;
   @InjectView(R.id.auto_delete_cards_labels) TextView autoDeleteCardLabel;

   private Observable<Boolean> stealthModeSwitcherObservable;
   private Observable<Boolean> lockSwitcherObservable;
   private Observable<Boolean> testConnectionObservable;

   private final AutoClearSmartCardItemProvider autoClearSmartCardItemProvider = new AutoClearSmartCardItemProvider();
   private final DisableDefaultCardItemProvider disableDefaultCardItemProvider = new DisableDefaultCardItemProvider();

   public WalletCardSettingsScreen(Context context) {
      super(context);
   }

   public WalletCardSettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      if (isInEditMode()) return;
      lockSwitcherObservable = RxCompoundButton.checkedChanges(lockSwitcher);
      stealthModeSwitcherObservable = RxCompoundButton.checkedChanges(stealthModeSwitcher);
      testConnectionObservable = RxCompoundButton.checkedChanges(testConnectionSwitcher);
   }

   @NonNull
   @Override
   public WalletCardSettingsPresenter createPresenter() {
      return new WalletCardSettingsPresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   @OnClick(R.id.item_reset_pin)
   protected void onResetPinClick() {
      presenter.resetPin();
   }

   @OnClick(R.id.item_disable_default_payment_card)
   protected void onDisableDefaultCardClick() {
      presenter.disableDefaultCardTimer();
   }

   @OnClick(R.id.item_auto_delete_cards)
   protected void onAutoDeleteCardsClick() {
      presenter.autoClearSmartCardClick();
   }

   @Override
   public void stealthModeStatus(boolean isEnabled) {
      stealthModeSwitcher.setChecked(isEnabled);
   }

   @Override
   public void lockStatus(boolean lock) {
      lockSwitcher.setChecked(lock);
   }

   @Override
   public void testConnection(boolean connected) {
      testConnectionSwitcher.setChecked(connected);
   }

   @Override
   public void disableDefaultPaymentValue(long millis) {
      disableDefaultPaymentCardAfterLabel.setText(disableDefaultCardItemProvider.provideTextByValue(millis));
   }

   @Override
   public void autoClearSmartCardValue(long millis) {
      autoDeleteCardLabel.setText(autoClearSmartCardItemProvider.provideTextByValue(millis));
   }

   @Override
   public Observable<Boolean> stealthModeStatus() {
      return stealthModeSwitcherObservable;
   }

   @Override
   public Observable<Boolean> lockStatus() {
      return lockSwitcherObservable;
   }

   @Override
   public Observable<Boolean> testConnection() {
      return testConnectionObservable;
   }


   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }
}

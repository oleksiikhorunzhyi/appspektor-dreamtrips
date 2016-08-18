package com.worldventures.dreamtrips.wallet.ui.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class WalletCardSettingsScreen extends WalletFrameLayout<WalletCardSettingsPresenter.Screen, WalletCardSettingsPresenter, WalletCardSettingsPath> implements WalletCardSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.stealth_mode_switcher) SwitchCompat stealthModeSwitcher;

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

   @Override
   public void stealthModeStatus(boolean isEnabled) {
      stealthModeSwitcher.setChecked(isEnabled);
   }

   @Override
   public Observable<Boolean> stealthModeStatus() {
      return RxCompoundButton.checkedChanges(stealthModeSwitcher);
   }


   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }
}

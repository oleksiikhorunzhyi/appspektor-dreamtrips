package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetOperationView;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class EnterPinUnassignScreen extends WalletLinearLayout<EnterPinUnassignPresenter.Screen, EnterPinUnassignPresenter, EnterPinUnassignPath> implements EnterPinUnassignPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public EnterPinUnassignScreen(Context context) {
      super(context);
   }

   public EnterPinUnassignScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public EnterPinUnassignPresenter createPresenter() {
      return new EnterPinUnassignPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            factoryResetDelegate::goBack,
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.retry,
            R.string.cancel,
            R.string.loading,
            true);
   }

   @Override
   public View getView() {
      return this;
   }
}
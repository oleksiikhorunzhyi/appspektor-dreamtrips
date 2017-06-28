package com.worldventures.dreamtrips.wallet.ui.settings.general.reset;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.SimpleCancelStrategy;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.OperationView;

public class FactoryResetScreen extends WalletLinearLayout<FactoryResetPresenter.Screen, FactoryResetPresenter, FactoryResetPath> implements FactoryResetPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   private DialogOperationScreen dialogOperationScreen;

   public FactoryResetScreen(Context context) {
      super(context);
   }

   public FactoryResetScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public FactoryResetPresenter createPresenter() {
      return new FactoryResetPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      if (dialogOperationScreen == null) dialogOperationScreen = new DialogOperationScreen(this);
      dialogOperationScreen.setCancelStrategy(new SimpleCancelStrategy());
      return dialogOperationScreen;
   }

   @OnClick(R.id.btn_cancel)
   public void onClickCancel() {
      presenter.goBack();
   }

   @Override
   public View getView() {
      return this;
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
}
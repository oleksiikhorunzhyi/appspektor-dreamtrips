package com.worldventures.dreamtrips.wallet.ui.settings.factory_reset;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.ExplicitCancelStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.SimpleCancelStrategy;

public class FactoryResetScreen extends WalletLinearLayout<FactoryResetPresenter.Screen, FactoryResetPresenter, FactoryResetPath> implements FactoryResetPresenter.Screen {

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
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      if (dialogOperationScreen == null) dialogOperationScreen = new DialogOperationScreen(this);
      dialogOperationScreen.setCancelStrategy(new ExplicitCancelStrategy(dialog -> getPresenter().cancelFactoryReset()));
      return dialogOperationScreen;
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public void restrictCancel() {
      if (dialogOperationScreen != null) {
         dialogOperationScreen.updateCancelStrategy(new SimpleCancelStrategy());
      }
   }
}
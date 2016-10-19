package com.worldventures.dreamtrips.wallet.ui.settings.factory_reset_success;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.OnClick;


public class FactoryResetSuccessScreen extends WalletLinearLayout<FactoryResetSuccessPresenter.Screen, FactoryResetSuccessPresenter, FactoryResetSuccessPath>
      implements FactoryResetSuccessPresenter.Screen {

   public FactoryResetSuccessScreen(Context context) {
      super(context);
   }

   public FactoryResetSuccessScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public FactoryResetSuccessPresenter createPresenter() {
      return new FactoryResetSuccessPresenter(getContext(), getInjector());
   }

   @OnClick(R.id.next_button)
   public void nextClick() {
      presenter.goToNext();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}

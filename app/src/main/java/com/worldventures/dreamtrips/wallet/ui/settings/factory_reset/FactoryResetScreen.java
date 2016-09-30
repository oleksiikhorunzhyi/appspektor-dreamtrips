package com.worldventures.dreamtrips.wallet.ui.settings.factory_reset;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;

public class FactoryResetScreen extends WalletFrameLayout<FactoryResetPresenter.Screen, FactoryResetPresenter, FactoryResetPath> implements FactoryResetPresenter.Screen {

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
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      if (dialogOperationScreen == null) dialogOperationScreen = new DialogOperationScreen(this);
      return dialogOperationScreen;
   }

}
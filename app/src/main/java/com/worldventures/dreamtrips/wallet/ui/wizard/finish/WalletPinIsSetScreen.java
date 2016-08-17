package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletPinIsSetScreen extends WalletFrameLayout<WalletPinIsSetPresenter.Screen, WalletPinIsSetPresenter, WalletPinIsSetPath> implements WalletPinIsSetPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WalletPinIsSetScreen(Context context) {
      super(context);
   }

   public WalletPinIsSetScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WalletPinIsSetPresenter createPresenter() {
      return new WalletPinIsSetPresenter(getContext(), getInjector(), getPath().smartCard);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @OnClick(R.id.next_button)
   public void nextClick() {
      presenter.activateSmartCard();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }
}

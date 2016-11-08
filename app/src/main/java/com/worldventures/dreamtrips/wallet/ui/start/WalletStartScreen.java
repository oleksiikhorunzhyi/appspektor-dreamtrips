package com.worldventures.dreamtrips.wallet.ui.start;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import static com.worldventures.dreamtrips.wallet.ui.start.WalletStartPresenter.Screen;

public class WalletStartScreen extends WalletLinearLayout<Screen, WalletStartPresenter, WalletStartPath> implements Screen {

   public WalletStartScreen(Context context) {
      super(context);
   }

   public WalletStartScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return false;
   }

   @NonNull
   @Override
   public WalletStartPresenter createPresenter() {
      return new WalletStartPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }
}

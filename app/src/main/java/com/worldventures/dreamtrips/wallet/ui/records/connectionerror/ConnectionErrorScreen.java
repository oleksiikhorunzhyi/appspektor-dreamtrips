package com.worldventures.dreamtrips.wallet.ui.records.connectionerror;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;

public class ConnectionErrorScreen extends WalletLinearLayout<ConnectionErrorPresenter.Screen, ConnectionErrorPresenter, ConnectionErrorPath> implements ConnectionErrorPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public ConnectionErrorScreen(Context context) {
      super(context);
   }

   public ConnectionErrorScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public ConnectionErrorPresenter createPresenter() {
      return new ConnectionErrorPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}

package com.worldventures.dreamtrips.wallet.ui.wizard.records.finish;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class PaymentSyncFinishScreen extends WalletLinearLayout<PaymentSyncFinishPresenter.Screen, PaymentSyncFinishPresenter, PaymentSyncFinishPath> implements PaymentSyncFinishPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public PaymentSyncFinishScreen(Context context) {
      super(context);
   }

   public PaymentSyncFinishScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if(isInEditMode()) return;
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public PaymentSyncFinishPresenter createPresenter() {
      return new PaymentSyncFinishPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @OnClick(R.id.btn_done)
   public void onClickDone() {
      presenter.onDone();
   }
}

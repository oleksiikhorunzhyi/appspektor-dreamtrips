package com.worldventures.dreamtrips.wallet.ui.wizard.pin_set_success;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class PinSetSuccessScreen extends WalletLinearLayout<PinSetSuccessPresenter.Screen, PinSetSuccessPresenter, PinSetSuccessPath>
      implements PinSetSuccessPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.next_button) TextView nextButton;
   @InjectView(R.id.success_label_text_view) TextView successText;

   public PinSetSuccessScreen(Context context) {
      super(context);
   }

   public PinSetSuccessScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public PinSetSuccessPresenter createPresenter() {
      return new PinSetSuccessPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> presenter.goToBack());
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

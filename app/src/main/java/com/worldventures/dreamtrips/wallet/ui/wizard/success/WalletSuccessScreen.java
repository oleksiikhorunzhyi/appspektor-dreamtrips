package com.worldventures.dreamtrips.wallet.ui.wizard.success;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletSuccessScreen extends WalletFrameLayout<WalletScreen, WalletSuccessPresenter, WalletSuccessPath> implements WalletScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.next_button) TextView nextButton;
   @InjectView(R.id.success_label_text_view) TextView successText;

   public WalletSuccessScreen(Context context) {
      super(context);
   }

   public WalletSuccessScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WalletSuccessPresenter createPresenter() {
      return new WalletSuccessPresenter(getContext(), getInjector(), getPath().nextPath);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> presenter.goToBack());
   }

   @Override
   public void setPath(WalletSuccessPath path) {
      super.setPath(path);
      toolbar.setTitle(path.title);
      nextButton.setText(path.buttonText);
      successText.setText(path.text);
   }

   @OnClick(R.id.next_button)
   public void nextClick() {
      presenter.goToNext();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }
}

package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class PairKeyScreen extends WalletLinearLayout<PairKeyPresenter.Screen, PairKeyPresenter, PairKeyPath> implements PairKeyPresenter.Screen{

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public PairKeyScreen(Context context) {
      super(context);
   }

   public PairKeyScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @NonNull
   @Override
   public PairKeyPresenter createPresenter() {
      return new PairKeyPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public void showError(@StringRes int messageId) {
      new MaterialDialog.Builder(getContext())
            .content(messageId)
            .positiveText(R.string.ok)
            .show();
   }

   @OnClick(R.id.button_next)
   public void onConnectToSmartCard() {
      presenter.tryToPairAndConnectSmartCard();
   }
}
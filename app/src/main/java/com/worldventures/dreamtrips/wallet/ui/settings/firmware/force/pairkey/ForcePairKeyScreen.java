package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.pairkey;

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

public class ForcePairKeyScreen extends WalletLinearLayout<ForcePairKeyPresenter.Screen, ForcePairKeyPresenter, ForcePairKeyPath> implements ForcePairKeyPresenter.Screen{

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public ForcePairKeyScreen(Context context) {
      super(context);
   }

   public ForcePairKeyScreen(Context context, AttributeSet attrs) {
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
   public ForcePairKeyPresenter createPresenter() {
      return new ForcePairKeyPresenter(getContext(), getInjector());
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
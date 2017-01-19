package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.factoryreset;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import butterknife.InjectView;
import rx.functions.Action1;

public class ForceFactoryResetScreen extends WalletLinearLayout<ForceFactoryResetPresenter.Screen, ForceFactoryResetPresenter, ForceFactoryResetPath>
      implements ForceFactoryResetPresenter.Screen, OperationScreen<Void> {

   @InjectView(R.id.progress) WalletProgressWidget progressView;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public ForceFactoryResetScreen(Context context) {
      super(context);
   }

   public ForceFactoryResetScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public ForceFactoryResetPresenter createPresenter() {
      return new ForceFactoryResetPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return this;
   }

   @Override
   public void showProgress(@Nullable String text) {
      progressView.setVisibility(VISIBLE);
      progressView.start();
   }

   @Override
   public void hideProgress() {
      progressView.stop();
      progressView.setVisibility(GONE);
   }

   @Override
   public Context context() {
      return getContext();
   }

   @Override
   public void showError(String message, @Nullable Action1<Void> action) {
      new MaterialDialog.Builder(getContext())
            .content(message)
            .positiveText(R.string.wallet_firmware_install_error_retry_action)
            .negativeText(R.string.wallet_firmware_install_error_cancel_action)
            .onPositive((dialog, which) -> {
               if (action != null) action.call(null);
            })
            .onNegative((dialog, which) -> presenter.finish())
            .show();
   }
}
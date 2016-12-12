package com.worldventures.dreamtrips.wallet.ui.wizard.associate;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import rx.functions.Action1;

public class ConnectSmartCardScreen extends WalletLinearLayout<ConnectSmartCardPresenter.Screen, ConnectSmartCardPresenter, ConnectSmartCardPath>
      implements ConnectSmartCardPresenter.Screen, OperationScreen<Void> {

   @InjectView(R.id.connection_progress) View downloadProgress;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public ConnectSmartCardScreen(Context context) {
      super(context);
   }

   public ConnectSmartCardScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
   }

   @NonNull
   @Override
   public ConnectSmartCardPresenter createPresenter() {
      return new ConnectSmartCardPresenter(getContext(), getInjector(), getPath().barcode, getPath().barcodeOrigin);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      //set color transparent for add space without white back arrow
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return this;
   }

   @Override
   public void showProgress(@Nullable String text) {
      Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.wallet_progress_anim);
      animation.setDuration(getResources().getInteger(R.integer.wallet_custom_loafing_animation_duration));
      downloadProgress.startAnimation(animation);
   }

   @Override
   public void hideProgress() {
   }

   @Override
   public void showError(String message, @Nullable Action1<Void> action) {
      new MaterialDialog.Builder(getContext())
            .content(message)
            .positiveText(R.string.ok)
            .dismissListener(dialog -> {
               if (action != null) action.call(null);
            })
            .show();
   }

   @Override
   public void showError(@StringRes int messageId) {
      showError(getString(messageId), null);
   }

   @Override
   public Context context() {
      return getContext();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}

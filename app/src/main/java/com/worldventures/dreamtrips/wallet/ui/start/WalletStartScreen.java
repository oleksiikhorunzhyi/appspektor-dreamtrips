package com.worldventures.dreamtrips.wallet.ui.start;

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

import static com.worldventures.dreamtrips.wallet.ui.start.WalletStartPresenter.Screen;

public class WalletStartScreen extends WalletLinearLayout<Screen, WalletStartPresenter, WalletStartPath>
      implements Screen, OperationScreen<Void> {

   @InjectView(R.id.progress) WalletProgressWidget progressView;
   @InjectView(R.id.toolbar) Toolbar toolbar;

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

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
   }

   @NonNull
   @Override
   public WalletStartPresenter createPresenter() {
      return new WalletStartPresenter(getContext(), getInjector());
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
   public Context context() {
      return getContext();
   }
}

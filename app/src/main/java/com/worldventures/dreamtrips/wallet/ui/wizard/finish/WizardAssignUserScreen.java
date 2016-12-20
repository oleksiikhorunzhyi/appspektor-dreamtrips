package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class WizardAssignUserScreen extends WalletLinearLayout<WizardAssignUserPresenter.Screen, WizardAssignUserPresenter, WizardAssignUserPath>
      implements WizardAssignUserPresenter.Screen, OperationScreen<Void> {

   @InjectView(R.id.assign_progress) WalletProgressWidget assignProgress;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WizardAssignUserScreen(Context context) {
      super(context);
   }

   public WizardAssignUserScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
   }

   @NonNull
   @Override
   public WizardAssignUserPresenter createPresenter() {
      return new WizardAssignUserPresenter(getContext(), getInjector(), getPath().smartCard);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return this;
   }

   @Override
   public void showProgress(@Nullable String text) {
      assignProgress.setVisibility(VISIBLE);
      assignProgress.start();
   }

   @Override
   public void hideProgress() {
      assignProgress.stop();
      assignProgress.setVisibility(GONE);
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

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}

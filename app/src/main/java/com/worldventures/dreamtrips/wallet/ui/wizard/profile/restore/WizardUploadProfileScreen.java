package com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardUploadProfileScreen extends WalletLinearLayout<WizardUploadProfilePresenter.Screen, WizardUploadProfilePresenter, WizardUploadProfilePath> implements WizardUploadProfilePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.progress) WalletProgressWidget progressWidget;

   private MaterialDialog retryUploadDataDialog = null;

   public WizardUploadProfileScreen(Context context) {
      super(context);
   }

   public WizardUploadProfileScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if(isInEditMode()) return;
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public WizardUploadProfilePresenter createPresenter() {
      return new WizardUploadProfilePresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }
   
   @Override
   public OperationView<SetupUserDataCommand> provideOperationSetupUserData() {
      return new ComposableOperationView<>(
            new WalletProgressView<>(progressWidget)
      );
   }

   @Override
   public void showRetryDialog() {
      if (retryUploadDataDialog == null) {
         retryUploadDataDialog = new MaterialDialog.Builder(getContext())
               .content(R.string.wallet_error_uploading_retry)
               .positiveText(R.string.retry)
               .negativeText(R.string.cancel)
               .onPositive((dialog, which) -> presenter.retryUpload())
               .build();
      }
      if(!retryUploadDataDialog.isShowing()) retryUploadDataDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if(retryUploadDataDialog != null) retryUploadDataDialog.dismiss();
      super.onDetachedFromWindow();
   }
}

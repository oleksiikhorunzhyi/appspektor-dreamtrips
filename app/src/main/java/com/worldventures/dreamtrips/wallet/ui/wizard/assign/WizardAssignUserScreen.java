package com.worldventures.dreamtrips.wallet.ui.wizard.assign;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardAssignUserScreen extends WalletLinearLayout<WizardAssignUserPresenter.Screen, WizardAssignUserPresenter, WizardAssignUserPath>
      implements WizardAssignUserPresenter.Screen {

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
      return new WizardAssignUserPresenter(getContext(), getInjector(), getPath().provisioningMode);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public OperationView<WizardCompleteCommand> provideOperationView() {
      return new ComposableOperationView<>(new WalletProgressView<>(assignProgress),
            ErrorViewFactory.<WizardCompleteCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(),
                        command -> presenter.onWizardComplete(),
                        command -> presenter.onWizardCancel()))
                  .build());
   }
}

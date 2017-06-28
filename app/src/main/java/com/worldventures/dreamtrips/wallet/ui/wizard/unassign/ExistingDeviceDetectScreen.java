package com.worldventures.dreamtrips.wallet.ui.wizard.unassign;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.helper.CardIdUtil;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ExistingDeviceDetectScreen extends WalletLinearLayout<ExistingDeviceDetectPresenter.Screen, ExistingDeviceDetectPresenter, ExistingDeviceDetectPath> implements ExistingDeviceDetectPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_sc_id) TextView tvSmartCardId;

   public ExistingDeviceDetectScreen(Context context) {
      super(context);
   }

   public ExistingDeviceDetectScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public ExistingDeviceDetectPresenter createPresenter() {
      return new ExistingDeviceDetectPresenter(getContext(), getInjector(), getPath().scId);
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }
   
   @OnClick(R.id.button_unpair)
   public void onClickUnpair() {
      presenter.repair();
   }

   @Override
   public OperationView<ReAssignCardCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_existing_device_detect_progress, false),
            ErrorViewFactory.<ReAssignCardCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), command -> presenter.retryReAssigning(), command -> { /*nothing*/}))
                  .build()
      );
   }

   @Override
   public void setSmartCardId(String scId) {
      tvSmartCardId.setText(CardIdUtil.pushZeroToSmartCardId(scId));
   }

   @Override
   public void showConfirmDialog(String scId) {
      new MaterialDialog.Builder(getContext())
            .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_existing_device_detect_unpair_dialog, scId)))
            .positiveText(R.string.wallet_continue_label)
            .onPositive((dialog, which) -> presenter.repairConfirmed())
            .negativeText(R.string.wallet_cancel_label)
            .show();
   }
}
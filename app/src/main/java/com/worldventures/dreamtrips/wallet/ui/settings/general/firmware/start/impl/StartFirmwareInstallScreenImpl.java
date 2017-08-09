package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.functions.Action1;

public class StartFirmwareInstallScreenImpl extends WalletBaseController<StartFirmwareInstallScreen, StartFirmwareInstallPresenter> implements StartFirmwareInstallScreen {

   @InjectView(R.id.progress) WalletProgressWidget progressView;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject StartFirmwareInstallPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_start_firmware_install, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public StartFirmwareInstallPresenter getPresenter() {
      return presenter;
   }

   @Override
   public OperationView<PrepareForUpdateCommand> provideOperationPrepareForUpdate() {
      return new ComposableOperationView<>(
            new WalletProgressView<>(progressView),
            ErrorViewFactory.<PrepareForUpdateCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        positivePreparingAction, negativePreparingAction))
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext(),
                        positivePreparingAction, negativePreparingAction))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(),
                        positivePreparingAction, negativePreparingAction))
                  .build()
      );
   }

   private final Action1<PrepareForUpdateCommand> positivePreparingAction = cmd -> getPresenter().prepareForUpdate();

   private final Action1<PrepareForUpdateCommand> negativePreparingAction = cmd -> getPresenter().finish();
}

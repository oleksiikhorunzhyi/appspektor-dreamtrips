package com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.DialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class WalletOfflineModeSettingsScreenImpl extends WalletBaseController<WalletOfflineModeSettingsScreen, WalletOfflineModeSettingsPresenter> implements WalletOfflineModeSettingsScreen {

   private WalletSwitcher offlineModeSwitcher;

   @Inject WalletOfflineModeSettingsPresenter presenter;

   private Observable<Boolean> enableOfflineModeObservable;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      final TextView tvPleaseNoteMessage = view.findViewById(R.id.offline_mode_please_note_label);
      tvPleaseNoteMessage.setText(ProjectTextUtils.fromHtml(getString(R.string.wallet_offline_mode_please_note_message)));
      offlineModeSwitcher = view.findViewById(R.id.offline_mode_switcher);
      enableOfflineModeObservable = RxCompoundButton.checkedChanges(offlineModeSwitcher).skip(1);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_offline_mode, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   public OperationView<SwitchOfflineModeCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(),
                  ProjectTextUtils.fromHtml(getString(R.string.wallet_offline_mode_progress_message)), false),
            ErrorViewFactory.<SwitchOfflineModeCommand>builder()
                  .addProvider(getNetworkUnavailableDialogProvider())
                  .defaultErrorView(getDefaultErrorDialogProvider())
                  .build()
      );
   }

   @Override
   public Observable<Boolean> observeOfflineModeSwitcher() {
      return enableOfflineModeObservable;
   }

   @Override
   public void showConfirmationDialog(boolean enable) {
      new MaterialDialog.Builder(getContext())
            .content(enable ? R.string.wallet_offline_mode_enable_message : R.string.wallet_offline_mode_disable_message)
            .positiveText(R.string.wallet_continue_label)
            .onPositive((dialog, which) -> getPresenter().switchOfflineMode())
            .negativeText(R.string.cancel)
            .onNegative((dialog, which) -> getPresenter().switchOfflineModeCanceled())
            .cancelable(false)
            .build()
            .show();
   }

   @Override
   public void setOfflineModeState(boolean enabled) {
      offlineModeSwitcher.setCheckedWithoutNotify(enabled);
   }

   @NonNull
   private DialogErrorView<SwitchOfflineModeCommand> getDefaultErrorDialogProvider() {
      return new DialogErrorView<SwitchOfflineModeCommand>(getContext()) {
         @Override
         protected MaterialDialog createDialog(SwitchOfflineModeCommand command, Throwable throwable, Context context) {
            return new MaterialDialog.Builder(getContext())
                  .content(R.string.wallet_offline_mode_error_default_message)
                  .positiveText(R.string.retry)
                  .onPositive((dialog, which) -> getPresenter().switchOfflineMode())
                  .negativeText(R.string.cancel)
                  .onNegative((dialog, which) -> getPresenter().switchOfflineModeCanceled())
                  .cancelable(false)
                  .build();
         }
      };
   }

   @NonNull
   private ErrorViewProvider<SwitchOfflineModeCommand> getNetworkUnavailableDialogProvider() {
      return new ErrorViewProvider<SwitchOfflineModeCommand>() {
         @Override
         public Class<? extends Throwable> forThrowable() {
            return NetworkUnavailableException.class;
         }

         @Nullable
         @Override
         public ErrorView<SwitchOfflineModeCommand> create(SwitchOfflineModeCommand switchOfflineModeCommand, Throwable parentThrowable, Throwable throwable) {
            return new DialogErrorView<SwitchOfflineModeCommand>(getContext()) {
               @Override
               protected MaterialDialog createDialog(SwitchOfflineModeCommand command, Throwable throwable, Context context) {
                  return new MaterialDialog.Builder(getContext())
                        .content(R.string.wallet_offline_mode_error_no_internet_message)
                        .positiveText(R.string.settings)
                        .onPositive((dialog, which) -> getPresenter().navigateToSystemSettings())
                        .negativeText(R.string.cancel)
                        .onNegative((dialog, which) -> getPresenter().switchOfflineModeCanceled())
                        .cancelable(false)
                        .dismissListener(dialog -> getPresenter().fetchOfflineModeState())
                        .build();
               }
            };
         }
      };
   }

   @Override
   public WalletOfflineModeSettingsPresenter getPresenter() {
      return presenter;
   }
}

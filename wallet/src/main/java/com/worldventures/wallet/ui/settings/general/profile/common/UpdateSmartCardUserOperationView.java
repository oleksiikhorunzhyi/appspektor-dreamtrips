package com.worldventures.wallet.ui.settings.general.profile.common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.settings.general.display.exception.MissingUserPhoneException;
import com.worldventures.wallet.service.command.settings.general.display.exception.MissingUserPhotoException;
import com.worldventures.wallet.service.profile.BaseUserUpdateCommand;
import com.worldventures.wallet.service.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.wallet.service.profile.UpdateSmartCardUserCommand;
import com.worldventures.wallet.service.profile.UploadProfileDataException;
import com.worldventures.wallet.ui.common.base.screen.LifecycleHolder;
import com.worldventures.wallet.ui.common.helper2.error.DetachableOperationView;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.util.FirstNameException;
import com.worldventures.wallet.util.LastNameException;
import com.worldventures.wallet.util.MiddleNameException;
import com.worldventures.wallet.util.NetworkUnavailableException;

import rx.functions.Action0;

public class UpdateSmartCardUserOperationView {

   public static class UpdateUser extends DetachableOperationView<UpdateSmartCardUserCommand> {
      public UpdateUser(Context context, WalletProfileDelegate profileDelegate, @Nullable Action0 confirmDisplayTypeChange, LifecycleHolder lifecycleHolder) {
         super(new SimpleDialogProgressView<>(context, R.string.wallet_long_operation_hint, false),
               ErrorViewFactory.<UpdateSmartCardUserCommand>builder()
                     .addProvider(new SimpleDialogErrorViewProvider<>(context, FirstNameException.class, R.string.wallet_edit_profile_first_name_format_detail))
                     .addProvider(new SimpleDialogErrorViewProvider<>(context, MiddleNameException.class, R.string.wallet_edit_profile_middle_name_format_detail))
                     .addProvider(new SimpleDialogErrorViewProvider<>(context, LastNameException.class, R.string.wallet_edit_profile_last_name_format_detail))
                     .addProvider(new SimpleDialogErrorViewProvider<>(context, NetworkUnavailableException.class, R.string.wallet_card_settings_profile_dialog_error_network_unavailable))
                     .addProvider(provideDisplayTypeExceptionHandler(context, confirmDisplayTypeChange,
                           MissingUserPhotoException.class, R.string.wallet_card_settings_profile_display_settings_exception_photo))
                     .addProvider(provideDisplayTypeExceptionHandler(context, confirmDisplayTypeChange,
                           MissingUserPhoneException.class, R.string.wallet_card_settings_profile_display_settings_exception_phone))
                     .addProvider(provideUploadDataExceptionHandler(context, profileDelegate))
                     .addProvider(new SCConnectionErrorViewProvider<>(context))
                     .addProvider(new SmartCardErrorViewProvider<>(context))
                     .build(),
               lifecycleHolder);
      }
   }

   public static class RetryHttpUpload extends DetachableOperationView<RetryHttpUploadUpdatingCommand> {
      public RetryHttpUpload(Context context, WalletProfileDelegate profileDelegate, LifecycleHolder lifecycleHolder) {
         super(new SimpleDialogProgressView<>(context, R.string.wallet_long_operation_hint, false),
               ErrorViewFactory.<RetryHttpUploadUpdatingCommand>builder()
                     .addProvider(new SimpleDialogErrorViewProvider<>(context, NetworkUnavailableException.class, R.string.wallet_card_settings_profile_dialog_error_network_unavailable))
                     .addProvider(provideUploadDataExceptionHandler(context, profileDelegate))
                     .build(),
               lifecycleHolder);
      }
   }

   private static <T> ErrorViewProvider<T> provideDisplayTypeExceptionHandler(Context context,
         @Nullable Action0 confirmDisplayTypeChange, Class<? extends Throwable> throwable, @StringRes int message) {
      final SimpleDialogErrorViewProvider<T> errorProvider = new SimpleDialogErrorViewProvider<>(
            context, throwable, message, command -> {
         if (confirmDisplayTypeChange != null) {
            confirmDisplayTypeChange.call();
         }
      }, t -> { /*nothing*/ });
      errorProvider.setPositiveText(R.string.wallet_continue_label);
      return errorProvider;
   }

   private static <T extends BaseUserUpdateCommand> ErrorViewProvider<T> provideUploadDataExceptionHandler(Context context, WalletProfileDelegate profileDelegate) {
      final SimpleDialogErrorViewProvider<T> errorProvider = new SimpleDialogErrorViewProvider<>(context,
            UploadProfileDataException.class,
            R.string.wallet_card_settings_profile_dialog_error_server_content,
            command -> profileDelegate.retryUploadToServer(command.getSmartCardId(), command.getNewUser()),
            command -> profileDelegate.cancelUploadServerUserData(command.getSmartCardId(), command.getNewUser()));
      errorProvider.setPositiveText(R.string.wallet_retry_label);
      return errorProvider;
   }
}

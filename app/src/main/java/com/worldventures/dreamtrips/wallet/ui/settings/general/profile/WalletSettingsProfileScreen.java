package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UploadProfileDataException;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.util.FirstNameException;
import com.worldventures.dreamtrips.wallet.util.LastNameException;
import com.worldventures.dreamtrips.wallet.util.MiddleNameException;
import com.worldventures.dreamtrips.wallet.util.MissedAvatarException;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;
import com.worldventures.dreamtrips.wallet.util.PhoneNumberCreator;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class WalletSettingsProfileScreen extends WalletLinearLayout<WalletSettingsProfilePresenter.Screen, WalletSettingsProfilePresenter, WalletSettingsProfilePath> implements WalletSettingsProfilePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.photo_preview) SimpleDraweeView previewPhotoView;
   @InjectView(R.id.first_name) EditText etFirstName;
   @InjectView(R.id.middle_name) EditText etMiddleName;
   @InjectView(R.id.last_name) EditText etLastName;
   @InjectView(R.id.et_phone_number) EditText etPhoneNumber;
   @InjectView(R.id.et_country_code) EditText etCountryCode;

   /**
    * TODO
    * Send to backend blocked
    * Add ability to change 1 item independently
    * notify other streams that it's ok
    */
   private SmartCardUser smartCardUser;
   private MediaPickerService mediaPickerService;

   public WalletSettingsProfileScreen(Context context) {
      super(context);
   }

   public WalletSettingsProfileScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      toolbar.inflateMenu(R.menu.menu_wallet_settings_profile);
      toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.done:
               getPresenter().handleDoneAction();
               break;
         }
         return false;
      });

      if (isInEditMode()) return;

      etLastName.setKeyListener(null);

      //noinspection all
      mediaPickerService = (MediaPickerService) getContext().getSystemService(MediaPickerService.SERVICE_NAME);
      mediaPickerService.setPhotoPickerListener(photoPickerListener);
      ImageUtils.applyGrayScaleColorFilter(previewPhotoView);
   }

   private PhotoPickerLayout.PhotoPickerListener photoPickerListener = new PhotoPickerLayout.PhotoPickerListener() {
      @Override
      public void onClosed() {
         presenter.setupInputMode();
      }

      @Override
      public void onOpened() {}
   };

   @OnClick(R.id.imageContainer)
   public void choosePhotoClick() {
      presenter.choosePhoto();
   }

   @OnTouch(value = {R.id.first_name, R.id.middle_name, R.id.last_name,
         R.id.et_country_code, R.id.et_phone_number})
   public boolean onClickProfileFields(View view, MotionEvent event) {
      if(event.getAction() == MotionEvent.ACTION_DOWN) mediaPickerService.hidePicker();
      return false;
   }

   @NonNull
   @Override
   public WalletSettingsProfilePresenter createPresenter() {
      return new WalletSettingsProfilePresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.handleBackAction();
   }

   @Override
   public void setPreviewPhoto(@Nullable SmartCardUserPhoto photo) {
      if (photo != null) {
         previewPhotoView.setImageURI(photo.uri());
      } // // TODO: 5/23/17 add placeholder
   }

   @Override
   public void setUser(SmartCardUser user) {
      this.smartCardUser = user;
      SmartCardUserPhone phone = user.phoneNumber();
      if (phone != null) {
         etCountryCode.setText(phone.code());
         etPhoneNumber.setText(phone.number());
      }

      etFirstName.setText(user.firstName());
      etFirstName.setSelection(etFirstName.length());

      etMiddleName.setText(user.middleName());
      etMiddleName.setSelection(etMiddleName.length());

      etLastName.setText(user.lastName());

      setPreviewPhoto(user.userPhoto());
   }

   @Override
   public void showRevertChangesDialog() {
      //Some changes were made. Are you sure you want go back without saving them
      new MaterialDialog.Builder(getContext()).content(R.string.wallet_card_settings_profile_dialog_changes_title)
            .positiveText(R.string.wallet_card_settings_profile_dialog_changes_positive)
            .negativeText(R.string.wallet_card_settings_profile_dialog_changes_negative)
            .onPositive((dialog, which) -> getPresenter().goBack())
            .onNegative((dialog, which) -> dialog.dismiss())
            .show();
   }

   @Override
   public String getFirstName() {
      return etFirstName.getText().toString();
   }

   @Override
   public String getMiddleName() {
      return etMiddleName.getText().toString();
   }

   @Override
   public String getLastName() {
      return etLastName.getText().toString();
   }

   @Override
   public SmartCardUser getCurrentUser() {
      return smartCardUser;
   }

   @Override
   @Nullable
   public SmartCardUserPhone userPhone() {
      return PhoneNumberCreator.create(etCountryCode.getText().toString().trim(), etPhoneNumber.getText().toString().trim());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_long_operation_hint, false),
            ErrorViewFactory.<UpdateSmartCardUserCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), FirstNameException.class, R.string.wallet_edit_profile_first_name_format_detail))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), MiddleNameException.class, R.string.wallet_edit_profile_middle_name_format_detail))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), LastNameException.class, R.string.wallet_edit_profile_last_name_format_detail))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), MissedAvatarException.class, R.string.wallet_edit_profile_avatar_not_chosen))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), NetworkUnavailableException.class, R.string.wallet_card_settings_profile_dialog_error_network_unavailable))
                  .addProvider(provideUploadDataExceptionHandler())
                  .build()
      );
   }

   @Override
   public OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<RetryHttpUploadUpdatingCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), NetworkUnavailableException.class, R.string.wallet_card_settings_profile_dialog_error_network_unavailable))
                  .addProvider(provideUploadDataExceptionHandler())
                  .build()
      );
   }

   private <T> ErrorViewProvider<T> provideUploadDataExceptionHandler() {
      final SimpleDialogErrorViewProvider<T> errorProvider = new SimpleDialogErrorViewProvider<>(getContext(),
            UploadProfileDataException.class,
            R.string.wallet_card_settings_profile_dialog_error_server_content,
            command -> getPresenter().retryUploadToServer(),
            command -> getPresenter().cancelUploadServerUserData());
      errorProvider.setPositiveText(R.string.retry);
      return errorProvider;
   }

   @Override
   public void pickPhoto() {
      mediaPickerService.pickPhoto();
   }

   @Override
   public void cropPhoto(Uri photoPath) {
      mediaPickerService.crop(photoPath);
   }

   @Override
   public Observable<Uri> observePickPhoto() {
      return mediaPickerService.observePicker();
   }

   @Override
   public Observable<File> observeCropper() {
      return mediaPickerService.observeCropper();
   }

   @Override
   public void hidePhotoPicker() {
      mediaPickerService.hidePicker();
   }

}

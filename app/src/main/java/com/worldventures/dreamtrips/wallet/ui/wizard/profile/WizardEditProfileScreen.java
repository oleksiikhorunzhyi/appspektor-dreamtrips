package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.EditText;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.util.FirstNameException;
import com.worldventures.dreamtrips.wallet.util.LastNameException;
import com.worldventures.dreamtrips.wallet.util.MiddleNameException;
import com.worldventures.dreamtrips.wallet.util.MissedAvatarException;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class WizardEditProfileScreen extends WalletLinearLayout<WizardEditProfilePresenter.Screen, WizardEditProfilePresenter, WizardEditProfilePath> implements WizardEditProfilePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.first_name) EditText firstName;
   @InjectView(R.id.middle_name) EditText middleName;
   @InjectView(R.id.last_name) EditText lastName;
   @InjectView(R.id.photo_preview) SimpleDraweeView previewPhotoView;
   @InjectView(R.id.et_phone_number) EditText etPhoneNumber;
   @InjectView(R.id.et_country_code) EditText etCountryCode;

   private MediaPickerService mediaPickerService;

   public WizardEditProfileScreen(Context context) {
      super(context);
   }

   public WizardEditProfileScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WizardEditProfilePresenter createPresenter() {
      return new WizardEditProfilePresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      if (isInEditMode()) return;
      //noinspection all
      mediaPickerService = (MediaPickerService) getContext().getSystemService(MediaPickerService.SERVICE_NAME);
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      mediaPickerService.setPhotoPickerListener(photoPickerListener);
      ImageUtils.applyGrayScaleColorFilter(previewPhotoView);
   }

   private PhotoPickerLayout.PhotoPickerListener photoPickerListener = new PhotoPickerLayout.PhotoPickerListener() {
      @Override
      public void onClosed() {
         presenter.setupInputMode();
      }

      @Override
      public void onOpened() {

      }
   };

   protected void navigateButtonClick() {
      presenter.goToBack();
   }

   @OnClick(R.id.next_button)
   public void nextClick() {
      presenter.setupUserData();
   }

   @OnClick(R.id.imageContainer)
   public void choosePhotoClick() {
      presenter.choosePhoto();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public OperationView<SetupUserDataCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_long_operation_hint, false),
            ErrorViewFactory.<SetupUserDataCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), FirstNameException.class, R.string.wallet_edit_profile_first_name_format_detail))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), MiddleNameException.class, R.string.wallet_edit_profile_middle_name_format_detail))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), LastNameException.class, R.string.wallet_edit_profile_last_name_format_detail))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), MissedAvatarException.class, R.string.wallet_edit_profile_avatar_not_chosen))
                  .build()
      );
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
   public Observable<String> observeCropper() {
      return mediaPickerService.observeCropper().map(File::getAbsolutePath);
   }

   @Override
   public void hidePhotoPicker() {
      mediaPickerService.hidePicker();
   }

   @Override
   public void setPreviewPhoto(String photoUrl) {
      previewPhotoView.setImageURI(photoUrl);
   }

   @Override
   public void setUserFullName(@NonNull String firstName, @NonNull String lastName) {
      this.firstName.setText(firstName);
      this.lastName.setText(lastName);
      this.firstName.setSelection(firstName.length());
      this.lastName.setSelection(lastName.length());
   }

   @Override
   public void setPhone(String countryCode, String number) {
      etCountryCode.setText(countryCode);
      etPhoneNumber.setText(number);
   }

   @NonNull
   @Override
   public String[] getUserName() {
      return new String[]{firstName.getText().toString().trim(), middleName.getText().toString().trim(),
            lastName.getText().toString().trim()};
   }

   @Override
   public String getCountryCode() {
      return etCountryCode.getText().toString();
   }

   @Override
   public String getPhoneNumber() {
      return etPhoneNumber.getText().toString();
   }

   @Override
   public void showConfirmationDialog(String fullName) {
      new MaterialDialog.Builder(getContext())
            .content(Html.fromHtml(getString(R.string.wallet_edit_profile_confirmation_dialog_message, fullName)))
            .contentGravity(GravityEnum.CENTER)
            .positiveText(R.string.wallet_edit_profile_confirmation_dialog_button_positive)
            .onPositive((dialog, which) -> presenter.onUserDataConfirmed())
            .negativeText(R.string.wallet_edit_profile_confirmation_dialog_button_negative)
            .onNegative((dialog, which) -> dialog.cancel())
            .build()
            .show();
   }
}

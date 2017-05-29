package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class WalletSettingsProfileScreen extends WalletLinearLayout<WalletSettingsProfilePresenter.Screen, WalletSettingsProfilePresenter, WalletSettingsProfilePath> implements WalletSettingsProfilePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.photo_preview) SimpleDraweeView previewPhotoView;
   @InjectView(R.id.first_name) EditText etFirstName;
   @InjectView(R.id.middle_name) EditText etMiddleName;
   @InjectView(R.id.last_name) EditText etLastName;
   @InjectView(R.id.et_phone_number) EditText etPhoneNumber;
   @InjectView(R.id.et_country_code) EditText etCountryCode;

   private Observable<String> firstNameObservable;
   private Observable<String> middleNameObservable;
   private Observable<String> codeObservable;
   private Observable<String> phoneObservable;

   /**
    * TODO
    * Send to backend blocked
    * Add ability to change 1 item independently
    * notify other streams that it's ok
    */

   private MediaPickerService mediaPickerService;
   private Dialog progressDialog;

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
      firstNameObservable = RxTextView.afterTextChangeEvents(etFirstName).map(event -> event.editable().toString());
      middleNameObservable = RxTextView.afterTextChangeEvents(etMiddleName).map(event -> event.editable().toString());
      codeObservable = RxTextView.afterTextChangeEvents(etCountryCode).map(event -> event.editable().toString());
      phoneObservable = RxTextView.afterTextChangeEvents(etPhoneNumber).map(event -> event.editable().toString());
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

   @OnClick(R.id.imageContainer)
   public void choosePhotoClick() {
      presenter.choosePhoto();
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
   public void setPreviewPhoto(String photoUrl) {
      previewPhotoView.setImageURI(photoUrl);
   }

   @Override
   public void setUserName(String firstName, String middleName, String lastName) {
      etFirstName.setText(firstName);
      etFirstName.setSelection(etFirstName.length());

      etMiddleName.setText(middleName);
      etMiddleName.setSelection(etMiddleName.length());

      etLastName.setText(lastName);
   }

   @Override
   public void setPhone(String countryCode, String number) {
      etCountryCode.setText(countryCode);
      etPhoneNumber.setText(number);
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
   public String getCountryCode() {
      return etCountryCode.getText().toString();
   }

   @Override
   public String getPhoneNumber() {
      return etPhoneNumber.getText().toString();
   }

   @Override
   public Observable<String> firstNameObservable() {
      return firstNameObservable;
   }

   @Override
   public Observable<String> middleNameObservable() {
      return middleNameObservable;
   }

   @Override
   public Observable<String> codeObservable() {
      return codeObservable;
   }

   @Override
   public Observable<String> phoneObservable() {
      return phoneObservable;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void showProgress() {
      hideProgress();

      progressDialog = new MaterialDialog.Builder(getContext())
            .content(R.string.loading)
            .progress(true, 0)
            .cancelable(false)
            .build();

      progressDialog.show();
   }

   @Override
   public void hideProgress() {
      if (progressDialog != null && progressDialog.isShowing()) {
         progressDialog.dismiss();
      }
   }

   @Override
   public void showError(Throwable throwable) {
      String text = getContext().getString(throwable instanceof FormatException ?
            R.string.wallet_add_card_details_error_message :
            R.string.wallet_card_settings_profile_dialog_error_smartcard_content);

      new MaterialDialog.Builder(getContext())
            .content(text)
            .cancelable(false)
            .title(R.string.wallet_card_settings_profile_dialog_error_smartcard_header)
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> {
               if (throwable instanceof FormatException) getPresenter().cancelUpdating();
               else getPresenter().handleDoneAction();
            })
            .onNegative((dialog, which) -> getPresenter().cancelUpdating())
            .negativeText(R.string.cancel)
            .build()
            .show();
   }

   @Override
   public void showUploadServerError() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_card_settings_profile_dialog_error_server_content)
            .cancelable(false)
            .positiveText(R.string.retry)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> getPresenter().retryUploadToServer())
            .onNegative((dialog, which) -> getPresenter().cancelUploadServerUserData())
            .build()
            .show();
   }

   @Override
   public void showNetworkUnavailableError() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_card_settings_profile_dialog_error_network_unavailable)
            .cancelable(false)
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> dialog.dismiss())
            .build()
            .show();
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

}

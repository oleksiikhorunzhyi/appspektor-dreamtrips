package com.worldventures.dreamtrips.wallet.ui.settings.profile;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class WalletSettingsProfileScreen extends WalletLinearLayout<WalletSettingsProfilePresenter.Screen, WalletSettingsProfilePresenter, WalletSettingsProfilePath> implements WalletSettingsProfilePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.photo_preview) SimpleDraweeView previewPhotoView;
   @InjectView(R.id.first_name) EditText firstName;
   @InjectView(R.id.middle_name) EditText middleName;
   @InjectView(R.id.last_name) EditText lastName;
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
               getPresenter().setupUserData();
               break;
         }
         return false;
      });

      if (isInEditMode()) return;
      //noinspection all
      mediaPickerService = (MediaPickerService) getContext().getSystemService(MediaPickerService.SERVICE_NAME);
      mediaPickerService.setPhotoPickerListener(photoPickerListener);
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

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @NonNull
   @Override
   public WalletSettingsProfilePresenter createPresenter() {
      return new WalletSettingsProfilePresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.goBack(false);
   }

   @Override
   public void setPreviewPhoto(Uri uri) {
      previewPhotoView.setImageURI(uri);
   }

   @Override
   public void setUserName(String firstName, String middleName, String lastName) {
      this.firstName.setText(firstName);
      this.middleName.setText(middleName);
      this.lastName.setText(lastName);
   }

   @Override
   public void showRevertChangesDialog() {
      //Some changes were made. Are you sure you want go back without saving them
      new MaterialDialog.Builder(getContext()).content(R.string.wallet_card_settings_profile_dialog_changes_title)
            .positiveText(R.string.wallet_card_settings_profile_dialog_changes_positive)
            .negativeText(R.string.wallet_card_settings_profile_dialog_changes_negative)
            .onPositive((dialog, which) -> getPresenter().goBack(true))
            .onNegative((dialog, which) -> dialog.dismiss())
            .show();
   }

   @Override
   public String getFirstName() {
      return firstName.getText().toString();
   }

   @Override
   public String getMiddleName() {
      return middleName.getText().toString();
   }

   @Override
   public String getLastName() {
      return lastName.getText().toString();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
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
   public void showUploadSmartCardFailDialog(String text) {
      new MaterialDialog.Builder(getContext())
            .content(text)
            .cancelable(false)
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> getPresenter().setupUserData())
            .onNegative((dialog, which) -> getPresenter().cancelUploadSmartUserData())
            .negativeText(R.string.cancel)
            .build()
            .show();
   }

   @Override
   public void showUploadServerFailDialog() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_card_settings_profile_dialog_error_server_content)
            .cancelable(false)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> getPresenter().uploadDataToServer())
            .onNegative((dialog, which) -> getPresenter().cancelUploadServerUserData())
            .build()
            .show();
   }

   @Override
   public void pickPhoto() {
      mediaPickerService.pickPhoto();
   }

   @Override
   public void cropPhoto(String photoPath) {
      mediaPickerService.crop(photoPath);
   }

   @Override
   public Observable<String> observePickPhoto() {
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

package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class WizardEditProfileScreen extends WalletLinearLayout<WizardEditProfilePresenter.Screen, WizardEditProfilePresenter, WizardEditProfilePath> implements WizardEditProfilePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.first_name) EditText firstName;
   @InjectView(R.id.middle_name) EditText middleName;
   @InjectView(R.id.last_name) EditText lastName;
   @InjectView(R.id.photo_preview) SimpleDraweeView previewPhotoView;

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
      return new WizardEditProfilePresenter(getContext(), getInjector(), getPath().getSmartCardId());
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

   @Override
   public void setPreviewPhoto(File photo) {
      previewPhotoView.setImageURI(Uri.fromFile(photo));
   }

   @Override
   public void setUserFullName(@NonNull String firstName, @NonNull String lastName) {
      this.firstName.setText(firstName);
      this.lastName.setText(lastName);
      this.firstName.setSelection(firstName.length());
      this.lastName.setSelection(lastName.length());
   }

   @NonNull
   @Override
   public String[] getUserName() {
      return new String[]{ firstName.getText().toString().trim(), middleName.getText().toString().trim(),
            lastName.getText().toString().trim() };
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}

package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;

public class WizardEditProfileScreen extends WalletFrameLayout<WizardEditProfilePresenter.Screen, WizardEditProfilePresenter, WizardEditProfilePath> implements WizardEditProfilePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.person_name) EditText personName;
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
      if (isInEditMode()) return;
      //noinspection all
      mediaPickerService = (MediaPickerService) getContext().getSystemService(MediaPickerService.SERVICE_NAME);
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
   }

   protected void navigateButtonClick() {
      presenter.goToBack();
   }

   @OnClick(R.id.next_button)
   public void nextClick() {
      presenter.setupUserData();
   }

   @OnEditorAction(R.id.person_name)
   public boolean actionNext(int action) {
      if (action != EditorInfo.IME_ACTION_NEXT) return false;
      presenter.setupUserData();
      return true;
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
   public void setUserFullName(@NonNull String fullName) {
      personName.setText(fullName);
      personName.setSelection(fullName.length());
   }

   @NonNull
   @Override
   public String getUserName() {
      return personName.getText().toString();
   }
}

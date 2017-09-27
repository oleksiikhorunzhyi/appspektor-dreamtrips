package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.impl;


import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.drawable.ScalingUtils;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.ScreenWalletSettingsProfileBinding;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.dreamtrips.wallet.service.WalletCropImageService;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.binding.LastPositionSelector;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfileScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.UpdateSmartCardUserOperationView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletPhotoProposalDialog;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletSuffixSelectingDialog;
import com.worldventures.dreamtrips.wallet.util.SmartCardAvatarHelper;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import rx.subjects.PublishSubject;

public class WalletSettingsProfileScreenImpl extends WalletBaseController<WalletSettingsProfileScreen, WalletSettingsProfilePresenter> implements WalletSettingsProfileScreen {
   /**
    * TODO
    * Send to backend blocked
    * Add ability to change 1 item independently
    * notify other streams that it's ok
    */
   private static final String PROFILE_STATE_KEY = "WalletSettingsProfileScreen#PROFILE_STATE_KEY";

   @Inject WalletSettingsProfilePresenter presenter;

   private WalletCropImageService cropImageService;
   private ScreenWalletSettingsProfileBinding binding;
   private ProfileViewModel profileViewModel = new ProfileViewModel();
   private WalletPhotoProposalDialog photoActionDialog;
   private WalletSuffixSelectingDialog suffixSelectingDialog;
   private Dialog scNonConnectionDialog;

   private PublishSubject<ProfileViewModel> observeProfileViewModel = PublishSubject.create();

   private MenuItem actionDoneMenuItem;

   public WalletSettingsProfileScreenImpl() {
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      binding = DataBindingUtil.bind(view);
      binding.setOnAvatarClick(v -> showDialog());
      binding.setProfile(profileViewModel);
      binding.setOnEditTextFocusChange(new LastPositionSelector());
      binding.toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      binding.toolbar.inflateMenu(R.menu.wallet_settings_profile);
      binding.setOnDisplaySettingsClick(v -> getPresenter().openDisplaySettings());
      actionDoneMenuItem = binding.toolbar.getMenu().findItem(R.id.done);
      binding.toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.done:
               getPresenter().handleDoneAction();
               break;
         }
         return false;
      });

      binding.lastName.setKeyListener(null);

      //noinspection all
      cropImageService = (WalletCropImageService) getContext().getSystemService(WalletCropImageService.SERVICE_NAME);
      SmartCardAvatarHelper.applyGrayScaleColorFilter(binding.photoPreview);
      binding.photoPreview.getHierarchy()
            .setPlaceholderImage(R.drawable.ic_wallet_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP);
      binding.photoPreview.getHierarchy()
            .setFailureImage(R.drawable.ic_wallet_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP);

      binding.tvSuffix.setOnClickListener(v -> showSuffixDialog());
   }

   private void showSuffixDialog() {
      if (suffixSelectingDialog == null) {
         suffixSelectingDialog = new WalletSuffixSelectingDialog(getContext());
         suffixSelectingDialog.setOnSelectedAction(s -> binding.tvSuffix.setText(s));
      }
      suffixSelectingDialog.show();
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   void onChoosePhotoClick(String initialPhotoUrl) {
      hideDialog();
      final MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(result -> {
         if (!result.isEmpty()) {
            getPresenter().handlePickedPhoto(result.getChosenImages().get(0));
         }
      });
      if (initialPhotoUrl != null) {
         mediaPickerDialog.show(initialPhotoUrl);
      } else {
         mediaPickerDialog.show();
      }
   }

   @Override
   public void dropPhoto() {
      profileViewModel.setChosenPhotoUri(null);
   }

   @Override
   public void showDialog() {
      SoftInputUtil.hideSoftInputMethod(getView());
      photoActionDialog = new WalletPhotoProposalDialog(getContext());
      photoActionDialog.setOnChoosePhotoAction(() -> getPresenter().choosePhoto());
      photoActionDialog.setOnDoNotAddPhotoAction(this::onDoNotAddClick);
      photoActionDialog.setOnCancelAction(this::hideDialog);
      photoActionDialog.show();
   }

   private void onDoNotAddClick() {
      hideDialog();
      getPresenter().doNotAdd();
   }

   @Override
   public boolean handleBack() {
      if (getPresenter().isDataChanged()) {
         getPresenter().handleBackOnDataChanged();
         return true;
      } else {
         return super.handleBack();
      }
   }

   @Override
   public void hideDialog() {
      if (photoActionDialog == null) return;
      photoActionDialog.hide();
      photoActionDialog = null;
   }

   @Override
   public void showRevertChangesDialog() {
      //Some changes were made. Are you sure you want go back without saving them
      new MaterialDialog.Builder(getContext()).content(R.string.wallet_card_settings_profile_dialog_changes_title)
            .positiveText(R.string.wallet_card_settings_profile_dialog_changes_positive)
            .negativeText(R.string.wallet_card_settings_profile_dialog_changes_negative)
            .onPositive((dialog, which) -> getPresenter().revertChanges())
            .onNegative((dialog, which) -> dialog.dismiss())
            .show();
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      observeNewAvatar();
   }

   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      outState.putParcelable(PROFILE_STATE_KEY, profileViewModel);
      super.onSaveInstanceState(outState);
   }

   @Override
   protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      setUser(savedInstanceState.getParcelable(PROFILE_STATE_KEY));
   }

   private void observeNewAvatar() {
      observeCropper()
            .compose(bindToLifecycle())
            .subscribe(file -> profileViewModel.setChosenPhotoUri(Uri.fromFile(file).toString()));
   }

   @Override
   public void setUser(ProfileViewModel model) {
      profileViewModel.removeOnPropertyChangedCallback(profileViewModelCallback);
      profileViewModel = model;
      binding.setProfile(model);
      profileViewModel.addOnPropertyChangedCallback(profileViewModelCallback);
   }

   private android.databinding.Observable.OnPropertyChangedCallback profileViewModelCallback = new android.databinding.Observable.OnPropertyChangedCallback() {
      @Override
      public void onPropertyChanged(android.databinding.Observable observable, int i) {
         observeProfileViewModel.onNext(profileViewModel);
      }
   };

   @Override
   public ProfileViewModel getUser() {
      return profileViewModel;
   }

   @Override
   public OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation(WalletProfileDelegate delegate) {
      return new UpdateSmartCardUserOperationView.UpdateUser(getContext(), delegate,
            () -> getPresenter().confirmDisplayTypeChange());
   }

   @Override
   public OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation(WalletProfileDelegate delegate) {
      return new UpdateSmartCardUserOperationView.RetryHttpUpload(getContext(), delegate);
   }

   @Override
   public void showSCNonConnectionDialog() {
      if (scNonConnectionDialog == null) {
         scNonConnectionDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_card_settings_cant_connected)
               .content(R.string.wallet_card_settings_message_cant_connected)
               .positiveText(R.string.ok)
               .build();
      }
      if (!scNonConnectionDialog.isShowing()) scNonConnectionDialog.show();
   }

   @Override
   public void pickPhoto(String initialPhotoUrl) {
      onChoosePhotoClick(initialPhotoUrl);
   }

   @Override
   public void cropPhoto(Uri photoPath) {
      cropImageService.cropImage(getActivity(), photoPath);
   }

   @Override
   public Observable<File> observeCropper() {
      return cropImageService.observeCropper();
   }

   @Override
   public void setDoneButtonEnabled(boolean enable) {
      if (actionDoneMenuItem != null) actionDoneMenuItem.setEnabled(enable);
   }

   @Override
   public PublishSubject<ProfileViewModel> observeChangesProfileFields() {
      return observeProfileViewModel;
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (scNonConnectionDialog != null) scNonConnectionDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public WalletSettingsProfilePresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_profile, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return true;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }
}

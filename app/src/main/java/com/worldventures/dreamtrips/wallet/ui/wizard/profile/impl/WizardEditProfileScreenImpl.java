package com.worldventures.dreamtrips.wallet.ui.wizard.profile.impl;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.drawable.ScalingUtils;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.ScreenWalletWizardPersonalInfoBinding;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.dreamtrips.wallet.service.WalletCropImageService;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletPhotoProposalDialog;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletSuffixSelectingDialog;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfileScreen;
import com.worldventures.dreamtrips.wallet.util.FirstNameException;
import com.worldventures.dreamtrips.wallet.util.LastNameException;
import com.worldventures.dreamtrips.wallet.util.MiddleNameException;
import com.worldventures.dreamtrips.wallet.util.SmartCardAvatarHelper;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.core.utils.ProjectTextUtils.fromHtml;
import static com.worldventures.dreamtrips.wallet.util.SCUserUtils.userFullName;

public class WizardEditProfileScreenImpl extends WalletBaseController<WizardEditProfileScreen, WizardEditProfilePresenter> implements WizardEditProfileScreen {

   private static final String PROFILE_STATE_KEY = "WizardEditProfileScreen#PROFILE_STATE_KEY";

   @Inject WizardEditProfilePresenter presenter;

   private ScreenWalletWizardPersonalInfoBinding binding;
   private ProfileViewModel viewModel = new ProfileViewModel();
   private WalletCropImageService cropImageService;
   private WalletPhotoProposalDialog photoActionDialog;
   private WalletSuffixSelectingDialog suffixSelectingDialog;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      binding = DataBindingUtil.bind(view);
      binding.setOnAvatarClick(v -> showDialog());
      binding.setOnNextClick(v -> getPresenter().setupUserData());
      binding.setOnEditTextFocusChange((v, hasFocus) -> {
         if (hasFocus) {
            EditText editText = (EditText) v;
            editText.setSelection(editText.length());
         }
      });
      //noinspection all
      cropImageService = (WalletCropImageService) getContext().getSystemService(WalletCropImageService.SERVICE_NAME);
      binding.toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      SmartCardAvatarHelper.applyGrayScaleColorFilter(binding.photoPreview);
      binding.photoPreview.getHierarchy()
            .setPlaceholderImage(R.drawable.ic_wallet_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP);
      binding.photoPreview.getHierarchy()
            .setFailureImage(R.drawable.ic_wallet_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP);
      binding.setProfile(viewModel);

      binding.tvSuffix.setOnClickListener(v -> showSuffixDialog());
   }

   private void showSuffixDialog() {
      if (suffixSelectingDialog == null) {
         suffixSelectingDialog = new WalletSuffixSelectingDialog(getContext());
         suffixSelectingDialog.setOnSelectedAction(s -> binding.tvSuffix.setText(s));
      }
      suffixSelectingDialog.show();
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_personal_info, viewGroup, false);
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
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      observeNewAvatar();
   }

   @Override
   public WizardEditProfilePresenter getPresenter() {
      return presenter;
   }

   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      outState.putParcelable(PROFILE_STATE_KEY, viewModel);
      super.onSaveInstanceState(outState);
   }

   @Override
   protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      setProfile(savedInstanceState.getParcelable(PROFILE_STATE_KEY));
   }

   protected void navigateButtonClick() {
      getPresenter().back();
   }

   void onChoosePhotoClick(String initialPhotoUrl) {
      hideDialog();
      final MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(attachment -> {
         if (!attachment.isEmpty()) {
            getPresenter().handlePickedPhoto(attachment.getChosenImages().get(0));
         }
      });
      if (initialPhotoUrl != null) {
         mediaPickerDialog.show(initialPhotoUrl);
      } else {
         mediaPickerDialog.show();
      }
   }

   void onDontAddClick() {
      hideDialog();
      getPresenter().doNotAdd();
   }

   private void observeNewAvatar() {
      observeCropper()
            .compose(bindUntilDetach())
            .subscribe(file -> viewModel.setChosenPhotoUri(Uri.fromFile(file).toString()));
   }

   @Override
   public void dropPhoto() {
      viewModel.setChosenPhotoUri(null);
   }

   @Override
   public void showDialog() {
      SoftInputUtil.hideSoftInputMethod(getView());
      photoActionDialog = new WalletPhotoProposalDialog(getContext());
      photoActionDialog.setOnChoosePhotoAction(() -> getPresenter().choosePhoto());
      photoActionDialog.setOnDoNotAddPhotoAction(this::onDontAddClick);
      photoActionDialog.setOnCancelAction(this::hideDialog);
      photoActionDialog.show();
   }

   @Override
   public void hideDialog() {
      if (photoActionDialog == null) return;
      photoActionDialog.hide();
      photoActionDialog = null;
   }

   @Override
   public OperationView<SetupUserDataCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_long_operation_hint, false),
            ErrorViewFactory.<SetupUserDataCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), FirstNameException.class, R.string.wallet_edit_profile_first_name_format_detail))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), MiddleNameException.class, R.string.wallet_edit_profile_middle_name_format_detail))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), LastNameException.class, R.string.wallet_edit_profile_last_name_format_detail))
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext(),
                        cmd -> getPresenter().onUserDataConfirmed(), cmd -> getPresenter().back()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), cmd -> getPresenter().onUserDataConfirmed()))
                  .build()
      );
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
   public void setProfile(ProfileViewModel model) {
      viewModel = model;
      binding.setProfile(model);
   }

   @Override
   public ProfileViewModel getProfile() {
      return viewModel;
   }

   @Override
   public void showConfirmationDialog(ProfileViewModel profileViewModel) {
      new MaterialDialog.Builder(getContext())
            .content(fromHtml(getString(R.string.wallet_edit_profile_confirmation_dialog_message,
                  userFullName(profileViewModel.getFirstName(), profileViewModel.getMiddleName(), profileViewModel.getLastNameWithSuffix()))))
            .contentGravity(GravityEnum.CENTER)
            .positiveText(R.string.wallet_edit_profile_confirmation_dialog_button_positive)
            .onPositive((dialog, which) -> getPresenter().onUserDataConfirmed())
            .negativeText(R.string.wallet_edit_profile_confirmation_dialog_button_negative)
            .onNegative((dialog, which) -> dialog.cancel())
            .build()
            .show();
   }
}

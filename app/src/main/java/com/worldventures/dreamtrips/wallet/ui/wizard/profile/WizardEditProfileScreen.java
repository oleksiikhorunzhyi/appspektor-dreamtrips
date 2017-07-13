package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.EditText;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.drawable.ScalingUtils;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.ScreenWalletWizardPersonalInfoBinding;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.WalletCropImageService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletPhotoProposalDialog;
import com.worldventures.dreamtrips.wallet.util.FirstNameException;
import com.worldventures.dreamtrips.wallet.util.LastNameException;
import com.worldventures.dreamtrips.wallet.util.MiddleNameException;

import java.io.File;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.core.utils.ProjectTextUtils.fromHtml;
import static com.worldventures.dreamtrips.wallet.util.SCUserUtils.userFullName;

public class WizardEditProfileScreen extends WalletLinearLayout<WizardEditProfilePresenter.Screen, WizardEditProfilePresenter, WizardEditProfilePath> implements WizardEditProfilePresenter.Screen {

   private static final String PROFILE_STATE_KEY = "WizardEditProfileScreen#PROFILE_STATE_KEY";

   private ScreenWalletWizardPersonalInfoBinding binding;
   private ProfileViewModel viewModel = new ProfileViewModel();
   private WalletCropImageService cropImageService;
   private WalletPhotoProposalDialog photoActionDialog;

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
      binding = DataBindingUtil.bind(this);
      binding.setOnAvatarClick(v -> showDialog());
      binding.setOnNextClick(v -> presenter.setupUserData());
      binding.setOnEditTextFocusChange((view, hasFocus) -> {
         if (hasFocus) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.length());
         }
      });
      //noinspection all
      cropImageService = (WalletCropImageService) getContext().getSystemService(WalletCropImageService.SERVICE_NAME);
      binding.toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      ImageUtils.applyGrayScaleColorFilter(binding.photoPreview);
      binding.photoPreview.getHierarchy()
            .setPlaceholderImage(R.drawable.ic_edit_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP);
      binding.photoPreview.getHierarchy()
            .setFailureImage(R.drawable.ic_edit_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP);
      binding.setProfile(viewModel);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (isInEditMode()) return;
      observeNewAvatar();
   }

   @Override
   protected Parcelable onSaveInstanceState() {
      Bundle bundle = (Bundle) super.onSaveInstanceState();
      bundle.putParcelable(PROFILE_STATE_KEY, viewModel);
      return bundle;
   }

   @Override
   protected void onRestoreInstanceState(Parcelable state) {
      super.onRestoreInstanceState(state);
      Bundle bundle = (Bundle) state;
      setProfile(bundle.getParcelable(PROFILE_STATE_KEY));
   }

   protected void navigateButtonClick() {
      presenter.back();
   }

   void onChoosePhotoClick(String initialPhotoUrl) {
      hideDialog();
      final MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(attachment -> {
         if (!attachment.isEmpty()) {
            presenter.handlePickedPhoto(attachment.getChosenImages().get(0));
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
      presenter.doNotAdd();
   }

   private void observeNewAvatar() {
      observeCropper()
            .compose(lifecycle())
            .subscribe(file -> viewModel.setChosenPhotoUri(Uri.fromFile(file).toString()));
   }

   @Override
   public void dropPhoto() {
      viewModel.setChosenPhotoUri(null);
   }

   @Override
   public void showDialog() {
      SoftInputUtil.hideSoftInputMethod(this);
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
                  .build()
      );
   }

   @Override
   public void pickPhoto(String initialPhotoUrl) {
      onChoosePhotoClick(initialPhotoUrl);
   }

   @Override
   public void cropPhoto(Uri photoPath) {
      cropImageService.cropImage(photoPath);
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
                  userFullName(profileViewModel.getFirstName(), profileViewModel.getMiddleName(), profileViewModel.getLastName()))))
            .contentGravity(GravityEnum.CENTER)
            .positiveText(R.string.wallet_edit_profile_confirmation_dialog_button_positive)
            .onPositive((dialog, which) -> presenter.onUserDataConfirmed())
            .negativeText(R.string.wallet_edit_profile_confirmation_dialog_button_negative)
            .onNegative((dialog, which) -> dialog.cancel())
            .build()
            .show();
   }
}

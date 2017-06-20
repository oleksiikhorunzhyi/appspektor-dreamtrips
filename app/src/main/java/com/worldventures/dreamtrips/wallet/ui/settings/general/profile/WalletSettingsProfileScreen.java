package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.drawable.ScalingUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.ScreenWalletSettingsProfileBinding;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UploadProfileDataException;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletPhotoProposalDialog;
import com.worldventures.dreamtrips.wallet.util.FirstNameException;
import com.worldventures.dreamtrips.wallet.util.LastNameException;
import com.worldventures.dreamtrips.wallet.util.MiddleNameException;
import com.worldventures.dreamtrips.wallet.util.MissedAvatarException;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;

import java.io.File;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import rx.subjects.PublishSubject;

public class WalletSettingsProfileScreen extends WalletLinearLayout<WalletSettingsProfilePresenter.Screen, WalletSettingsProfilePresenter, WalletSettingsProfilePath> implements WalletSettingsProfilePresenter.Screen {
   /**
    * TODO
    * Send to backend blocked
    * Add ability to change 1 item independently
    * notify other streams that it's ok
    */
   public static final String PROFILE_STATE_KEY = "WalletSettingsProfileScreen#PROFILE_STATE_KEY";
   private MediaPickerService mediaPickerService;
   private ScreenWalletSettingsProfileBinding binding;
   private ProfileViewModel profileViewModel = new ProfileViewModel();
   private WalletPhotoProposalDialog photoActionDialog;

   private PublishSubject<ProfileViewModel> observeProfileViewModel = PublishSubject.create();

   private MenuItem actionDoneMenuItem;

   public WalletSettingsProfileScreen(Context context) {
      super(context);
   }

   public WalletSettingsProfileScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      binding = DataBindingUtil.bind(this);
      binding.setOnAvatarClick(v -> showDialog());
      binding.setProfile(profileViewModel);
      binding.setOnEditTextFocusChange((view, hasFocus) -> {
         if (hasFocus) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.length());
         }
      });
      binding.toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      binding.toolbar.inflateMenu(R.menu.menu_wallet_settings_profile);
      actionDoneMenuItem = binding.toolbar.getMenu().findItem(R.id.done);
      binding.toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.done:
               getPresenter().handleDoneAction();
               break;
         }
         return false;
      });

      if (isInEditMode()) return;

      binding.lastName.setKeyListener(null);

      //noinspection all
      mediaPickerService = (MediaPickerService) getContext().getSystemService(MediaPickerService.SERVICE_NAME);
      mediaPickerService.setPhotoPickerListener(photoPickerListener);
      ImageUtils.applyGrayScaleColorFilter(binding.photoPreview);
      binding.photoPreview.getHierarchy().setPlaceholderImage(R.drawable.ic_edit_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP);
      binding.photoPreview.getHierarchy().setFailureImage(R.drawable.ic_edit_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP);
   }

   private PhotoPickerLayout.PhotoPickerListener photoPickerListener = new PhotoPickerLayout.PhotoPickerListener() {
      @Override
      public void onClosed() {
         presenter.setupInputMode();
      }

      @Override
      public void onOpened() {}
   };

   @NonNull
   @Override
   public WalletSettingsProfilePresenter createPresenter() {
      return new WalletSettingsProfilePresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.handleBackAction();
   }

   void onChoosePhotoClick() {
      hideDialog();
      presenter.choosePhoto();
   }

   void onDontAddClick() {
      hideDialog();
      presenter.doNotAdd();
   }

   @Override
   public void dropPhoto() {
      profileViewModel.setChosenPhotoUri(null);
   }

   @Override
   public void showDialog() {
      photoActionDialog = new WalletPhotoProposalDialog(getContext());
      photoActionDialog.setOnChoosePhotoAction(this::onChoosePhotoClick);
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
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeNewAvatar();
   }

   @Override
   protected Parcelable onSaveInstanceState() {
      Bundle bundle = (Bundle) super.onSaveInstanceState();
      bundle.putParcelable(PROFILE_STATE_KEY, profileViewModel);
      return bundle;
   }

   @Override
   protected void onRestoreInstanceState(Parcelable state) {
      super.onRestoreInstanceState(state);
      Bundle bundle = (Bundle) state;
      setUser(bundle.getParcelable(PROFILE_STATE_KEY));
   }

   private void observeNewAvatar() {
      observeCropper()
            .compose(lifecycle())
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
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
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

   @Override
   public void setDoneButtonEnabled(boolean enable) {
      if (actionDoneMenuItem != null) actionDoneMenuItem.setEnabled(enable);
   }

   @Override
   public PublishSubject<ProfileViewModel> observeChangesProfileFields() {
      return observeProfileViewModel;
   }
}

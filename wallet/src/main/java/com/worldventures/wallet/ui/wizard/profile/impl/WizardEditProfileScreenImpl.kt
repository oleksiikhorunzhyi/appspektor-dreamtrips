package com.worldventures.wallet.ui.wizard.profile.impl

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.drawable.ScalingUtils
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog
import com.worldventures.core.ui.util.SoftInputUtil
import com.worldventures.core.utils.ProjectTextUtils.fromHtml
import com.worldventures.wallet.R
import com.worldventures.wallet.databinding.ScreenWalletWizardPersonalInfoBinding
import com.worldventures.wallet.service.WalletCropImageService
import com.worldventures.wallet.service.command.SetupUserDataCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.bindView
import com.worldventures.wallet.ui.common.binding.LastPositionSelector
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.settings.general.profile.common.WalletPhotoProposalDialog
import com.worldventures.wallet.ui.settings.general.profile.common.WalletSuffixSelectingDialog
import com.worldventures.wallet.ui.wizard.profile.WizardEditProfilePresenter
import com.worldventures.wallet.ui.wizard.profile.WizardEditProfileScreen
import com.worldventures.wallet.util.FirstNameException
import com.worldventures.wallet.util.LastNameException
import com.worldventures.wallet.util.MiddleNameException
import com.worldventures.wallet.util.SCUserUtils.userFullName
import com.worldventures.wallet.util.SmartCardAvatarHelper
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import javax.inject.Inject

private const val STATE_KEY_PHOTO_PICKER_SHOWING = "WalletSettingsProfileScreen#STATE_KEY_PHOTO_PICKER_SHOWING"
private const val STATE_KEY_PHOTO_PICKER_ORIGINAL_PIC = "WalletSettingsProfileScreen#STATE_KEY_PHOTO_PICKER_ORIGINAL_PIC"

class WizardEditProfileScreenImpl(bundle: Bundle?) : WalletBaseController<WizardEditProfileScreen, WizardEditProfilePresenter>(bundle), WizardEditProfileScreen {

   @Inject internal lateinit var presenter: WizardEditProfilePresenter

   private lateinit var binding: ScreenWalletWizardPersonalInfoBinding
   private lateinit var cropImageService: WalletCropImageService
   private var photoActionDialog: WalletPhotoProposalDialog? = null
   private var suffixSelectingDialog: WalletSuffixSelectingDialog? = null
   private var mediaPickerDialog: MediaPickerDialog? = null
   private var originalPhotoPickerDialogPic: String? = null
   private var setupUserOperationView: OperationView<SetupUserDataCommand>? = null

   constructor() : this(null)

   override var profile: ProfileViewModel = ProfileViewModel()
      set(value) {
         field = value
         binding.profile = value
      }

   override val provisionMode: ProvisioningMode
      get() = args.getSerializable(KEY_PROVISION_MODE) as ProvisioningMode

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      binding = bindView(view)
      binding.setOnAvatarClick { showDialog() }
      binding.setOnNextClick { getPresenter().setupUserData() }
      binding.onEditTextFocusChange = LastPositionSelector()

      cropImageService = context.getSystemService(WalletCropImageService.SERVICE_NAME) as WalletCropImageService
      binding.toolbar.setNavigationOnClickListener { navigateButtonClick() }
      SmartCardAvatarHelper.applyGrayScaleColorFilter(binding.photoPreview)
      binding.photoPreview.hierarchy
            .setPlaceholderImage(R.drawable.ic_wallet_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP)
      binding.photoPreview.hierarchy
            .setFailureImage(R.drawable.ic_wallet_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP)
      binding.profile = profile

      binding.tvSuffix.setOnClickListener { showSuffixDialog() }
   }

   private fun showSuffixDialog() {
      if (suffixSelectingDialog == null) {
         suffixSelectingDialog = WalletSuffixSelectingDialog(context)
         suffixSelectingDialog?.setOnSelectedAction { binding.tvSuffix.text = it }
      }
      suffixSelectingDialog?.show()
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_personal_info, viewGroup, false)
   }

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   override fun onAttach(view: View) {
      super.onAttach(view)
      observeNewAvatar()
   }

   override fun onDestroyView(view: View) {
      super.onDestroyView(view)
      mediaPickerDialog?.dismiss()
      mediaPickerDialog = null
   }

   override fun getPresenter() = presenter

   override fun onSaveViewState(view: View, outState: Bundle) {
      outState.putParcelable(PROFILE_STATE_KEY, profile)
      outState.putBoolean(STATE_KEY_PHOTO_PICKER_SHOWING, mediaPickerDialog != null)
      outState.putString(STATE_KEY_PHOTO_PICKER_ORIGINAL_PIC, originalPhotoPickerDialogPic)
      super.onSaveViewState(view, outState)
   }

   override fun onRestoreViewState(view: View, savedViewState: Bundle) {
      super.onRestoreViewState(view, savedViewState)
      profile = savedViewState.getParcelable(PROFILE_STATE_KEY)
      val mediaDialogShowing = savedViewState.getBoolean(STATE_KEY_PHOTO_PICKER_SHOWING)
      if (mediaDialogShowing) {
         pickPhoto(savedViewState.getString(STATE_KEY_PHOTO_PICKER_ORIGINAL_PIC))
      }
      super.onRestoreViewState(view, savedViewState)
   }

   private fun navigateButtonClick() = getPresenter().back()

   private fun onChoosePhotoClick(initialPhotoUrl: String?) {
      hideDialog()
      this.mediaPickerDialog?.dismiss()
      originalPhotoPickerDialogPic = initialPhotoUrl
      val mediaPickerDialog = MediaPickerDialog(context)
      mediaPickerDialog.setOnDoneListener { attachment ->
         if (!attachment.isEmpty) {
            getPresenter().handlePickedPhoto(attachment.chosenImages[0])
         }
      }
      if (initialPhotoUrl != null) {
         mediaPickerDialog.show(initialPhotoUrl)
      } else {
         mediaPickerDialog.show()
      }
      mediaPickerDialog.setOnDismissListener { this.mediaPickerDialog = null }
      this.mediaPickerDialog = mediaPickerDialog
   }

   private fun onDontAddPhotoClick() {
      hideDialog()
      getPresenter().doNotAdd()
   }

   private fun observeNewAvatar() {
      observeCropper()
            .compose(bindUntilDetach())
            .subscribe { profile.chosenPhotoUri = Uri.fromFile(it).toString() }
   }

   override fun dropPhoto() {
      profile.chosenPhotoUri = null
   }

   override fun showDialog() {
      SoftInputUtil.hideSoftInputMethod(view)
      photoActionDialog = WalletPhotoProposalDialog(context)
      photoActionDialog?.setOnChoosePhotoAction { getPresenter().choosePhoto() }
      photoActionDialog?.setOnDoNotAddPhotoAction(this::onDontAddPhotoClick)
      photoActionDialog?.setOnCancelAction(this::hideDialog)
      photoActionDialog?.show()
   }

   override fun hideDialog() {
      photoActionDialog?.hide()
      photoActionDialog = null
   }

   override fun provideOperationView(): OperationView<SetupUserDataCommand> {
      var view = setupUserOperationView
      if (view == null) {
         view = ComposableOperationView(
               SimpleDialogProgressView(context, R.string.wallet_long_operation_hint, false),
               ErrorViewFactory.builder<SetupUserDataCommand>()
                     .addProvider(SimpleDialogErrorViewProvider(context,
                           FirstNameException::class.java, R.string.wallet_edit_profile_first_name_format_detail))
                     .addProvider(SimpleDialogErrorViewProvider(context,
                           MiddleNameException::class.java, R.string.wallet_edit_profile_middle_name_format_detail))
                     .addProvider(SimpleDialogErrorViewProvider(context,
                           LastNameException::class.java, R.string.wallet_edit_profile_last_name_format_detail))
                     .addProvider(SCConnectionErrorViewProvider(context,
                           { presenter.onUserDataConfirmed() }) { })
                     .addProvider(SmartCardErrorViewProvider(context) { presenter.onUserDataConfirmed() })
                     .build()
         )
      }
      setupUserOperationView = view
      return view
   }

   override fun pickPhoto(initialPhotoUrl: String?) = onChoosePhotoClick(initialPhotoUrl)

   override fun cropPhoto(photoPath: Uri) = cropImageService.cropImage(activity, photoPath)

   override fun observeCropper() = cropImageService.observeCropper()

   override fun showConfirmationDialog(profileViewModel: ProfileViewModel) {
      MaterialDialog.Builder(context)
            .content(fromHtml(getString(R.string.wallet_edit_profile_confirmation_dialog_message,
                  userFullName(profileViewModel.firstName, profileViewModel.middleName, profileViewModel.lastName))))
            .contentGravity(GravityEnum.CENTER)
            .positiveText(R.string.wallet_edit_profile_confirmation_dialog_button_positive)
            .onPositive { _, _ -> getPresenter().onUserDataConfirmed() }
            .negativeText(R.string.wallet_edit_profile_confirmation_dialog_button_negative)
            .onNegative { dialog, _ -> dialog.cancel() }
            .build()
            .show()
   }

   override fun screenModule() = WizardEditProfileScreenModule()

   companion object {

      private val PROFILE_STATE_KEY = "WizardEditProfileScreen#PROFILE_STATE_KEY"
      private val KEY_PROVISION_MODE = "WizardEditProfileScreen#PROVISION_MODE_KEY"

      fun create(provisioningMode: ProvisioningMode): WizardEditProfileScreenImpl {
         val args = Bundle()
         args.putSerializable(KEY_PROVISION_MODE, provisioningMode)
         return WizardEditProfileScreenImpl(args)
      }
   }
}

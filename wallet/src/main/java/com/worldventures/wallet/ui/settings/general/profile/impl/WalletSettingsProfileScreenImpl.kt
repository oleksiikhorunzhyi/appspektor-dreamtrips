package com.worldventures.wallet.ui.settings.general.profile.impl

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.drawable.ScalingUtils
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog
import com.worldventures.core.ui.util.SoftInputUtil
import com.worldventures.wallet.R
import com.worldventures.wallet.databinding.ScreenWalletSettingsProfileBinding
import com.worldventures.wallet.service.WalletCropImageService
import com.worldventures.wallet.service.profile.RetryHttpUploadUpdatingCommand
import com.worldventures.wallet.service.profile.UpdateSmartCardUserCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.binding.LastPositionSelector
import com.worldventures.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter
import com.worldventures.wallet.ui.settings.general.profile.WalletSettingsProfileScreen
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.settings.general.profile.common.UpdateSmartCardUserOperationView
import com.worldventures.wallet.ui.settings.general.profile.common.WalletPhotoProposalDialog
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate
import com.worldventures.wallet.ui.settings.general.profile.common.WalletSuffixSelectingDialog
import com.worldventures.wallet.util.SmartCardAvatarHelper
import io.techery.janet.operationsubscriber.view.OperationView
import rx.Observable
import rx.subjects.PublishSubject
import java.io.File
import javax.inject.Inject

private const val PROFILE_STATE_KEY = "WalletSettingsProfileScreen#PROFILE_STATE_KEY"
private const val PROFILE_ORIGIN_STATE_KEY = "WalletSettingsProfileScreen#PROFILE_ORIGIN_STATE_KEY"

@Suppress("UnsafeCallOnNullableType")
class WalletSettingsProfileScreenImpl : WalletBaseController<WalletSettingsProfileScreen, WalletSettingsProfilePresenter>(), WalletSettingsProfileScreen {

   @Inject lateinit var screenPresenter: WalletSettingsProfilePresenter

   private lateinit var cropImageService: WalletCropImageService
   private lateinit var binding: ScreenWalletSettingsProfileBinding

   private var originProfileModule: ProfileViewModel = ProfileViewModel()

   private var photoActionDialog: WalletPhotoProposalDialog? = null
   private var suffixSelectingDialog: WalletSuffixSelectingDialog? = null
   private var scNonConnectionDialog: Dialog? = null

   private val observeProfileViewModel = PublishSubject.create<ProfileViewModel>()

   private lateinit var actionDoneMenuItem: MenuItem

   private val profileViewModelCallback = object : android.databinding.Observable.OnPropertyChangedCallback() {
      override fun onPropertyChanged(observable: android.databinding.Observable, i: Int) {
         observeProfileViewModel.onNext(binding.profile)
      }
   }

   override val isDataChanged: Boolean
      get() = originProfileModule != binding.profile

   override var profile: ProfileViewModel
      get() = binding.profile!!
      set(value) = setProfileModel(value)

   @Suppress("UnsafeCast")
   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      binding = DataBindingUtil.bind(view)
      binding.setOnAvatarClick { showDialog() }
      binding.profile = originProfileModule.copy()
      binding.onEditTextFocusChange = LastPositionSelector()
      binding.toolbar.setNavigationOnClickListener { onNavigationClick() }
      binding.toolbar.inflateMenu(R.menu.wallet_settings_profile)
      binding.setOnDisplaySettingsClick { presenter.openDisplaySettings() }
      actionDoneMenuItem = binding.toolbar.menu.findItem(R.id.done)
      binding.toolbar.setOnMenuItemClickListener { item ->
         if (item.itemId == R.id.done) {
            presenter.handleDoneAction()
         }
         false
      }

      binding.lastName.keyListener = null

      cropImageService = context.getSystemService(WalletCropImageService.SERVICE_NAME) as WalletCropImageService
      SmartCardAvatarHelper.applyGrayScaleColorFilter(binding.photoPreview)
      binding.photoPreview.hierarchy
            .setPlaceholderImage(R.drawable.ic_wallet_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP)
      binding.photoPreview.hierarchy
            .setFailureImage(R.drawable.ic_wallet_profile_silhouette, ScalingUtils.ScaleType.CENTER_CROP)

      binding.tvSuffix.setOnClickListener { showSuffixDialog() }
   }

   private fun showSuffixDialog() {
      if (suffixSelectingDialog == null) {
         suffixSelectingDialog = WalletSuffixSelectingDialog(context)
         suffixSelectingDialog!!.setOnSelectedAction { s -> binding.tvSuffix.text = s }
      }
      suffixSelectingDialog!!.show()
   }

   private fun onNavigationClick() {
      presenter.goBack()
   }

   private fun onChoosePhotoClick(initialPhotoUrl: String?) {
      hideDialog()
      val mediaPickerDialog = MediaPickerDialog(context)
      mediaPickerDialog.setOnDoneListener { result ->
         if (!result.isEmpty) {
            presenter.handlePickedPhoto(result.chosenImages[0])
         }
      }
      if (initialPhotoUrl != null) {
         mediaPickerDialog.show(initialPhotoUrl)
      } else {
         mediaPickerDialog.show()
      }
   }

   override fun dropPhoto() {
      binding.profile?.chosenPhotoUri = null
   }

   override fun showDialog() {
      SoftInputUtil.hideSoftInputMethod(view)
      photoActionDialog = WalletPhotoProposalDialog(context)
      photoActionDialog!!.setOnChoosePhotoAction { presenter.choosePhoto() }
      photoActionDialog!!.setOnDoNotAddPhotoAction { this.onDoNotAddClick() }
      photoActionDialog!!.setOnCancelAction { this.hideDialog() }
      photoActionDialog!!.show()
   }

   private fun onDoNotAddClick() {
      hideDialog()
      presenter.doNotAdd()
   }

   override fun handleBack(): Boolean {
      return if (isDataChanged) {
         presenter.handleBackOnDataChanged()
         true
      } else {
         super.handleBack()
      }
   }

   override fun hideDialog() {
      if (photoActionDialog == null) {
         return
      }
      photoActionDialog!!.hide()
      photoActionDialog = null
   }

   override fun showRevertChangesDialog() {
      //Some changes were made. Are you sure you want go back without saving them
      MaterialDialog.Builder(context).content(R.string.wallet_card_settings_profile_dialog_changes_title)
            .positiveText(R.string.wallet_card_settings_profile_dialog_changes_positive)
            .negativeText(R.string.wallet_card_settings_profile_dialog_changes_negative)
            .onPositive { _, _ -> presenter.revertChanges() }
            .onNegative { dialog, _ -> dialog.dismiss() }
            .show()
   }

   override fun onAttach(view: View) {
      super.onAttach(view)
      observeNewAvatar()
   }

   override fun onSaveViewState(view: View, outState: Bundle) {
      outState.putParcelable(PROFILE_STATE_KEY, binding.profile)
      outState.putParcelable(PROFILE_ORIGIN_STATE_KEY, originProfileModule)
      super.onSaveViewState(view, outState)
   }

   override fun onRestoreViewState(view: View, savedViewState: Bundle) {
      setProfileModel(savedViewState.getParcelable(PROFILE_ORIGIN_STATE_KEY), savedViewState.getParcelable(PROFILE_STATE_KEY))
      super.onRestoreViewState(view, savedViewState)
   }

   private fun observeNewAvatar() {
      observeCropper()
            .compose(bindUntilDetach())
            .subscribe { file -> binding.profile?.chosenPhotoUri = Uri.fromFile(file).toString() }
   }

   private fun setProfileModel(model: ProfileViewModel) {
      setProfileModel(model, model.copy())
   }

   private fun setProfileModel(origin: ProfileViewModel, forBinding: ProfileViewModel) {
      originProfileModule = origin
      binding.profile?.removeOnPropertyChangedCallback(profileViewModelCallback)
      binding.profile = forBinding
      forBinding.addOnPropertyChangedCallback(profileViewModelCallback)
   }

   override fun discardChanges() {
      setProfileModel(originProfileModule)
   }

   override fun provideUpdateSmartCardOperation(delegate: WalletProfileDelegate): OperationView<UpdateSmartCardUserCommand> {
      return UpdateSmartCardUserOperationView.UpdateUser(context, delegate
      ) { presenter.confirmDisplayTypeChange() }
   }

   override fun provideHttpUploadOperation(delegate: WalletProfileDelegate): OperationView<RetryHttpUploadUpdatingCommand> =
         UpdateSmartCardUserOperationView.RetryHttpUpload(context, delegate)

   override fun showSCNonConnectionDialog() {
      if (scNonConnectionDialog == null) {
         scNonConnectionDialog = MaterialDialog.Builder(context)
               .title(R.string.wallet_card_settings_cant_connected)
               .content(R.string.wallet_card_settings_message_cant_connected)
               .positiveText(R.string.wallet_ok)
               .build()
      }
      if (!scNonConnectionDialog!!.isShowing) {
         scNonConnectionDialog!!.show()
      }
   }

   override fun pickPhoto(initialPhotoUrl: String) {
      onChoosePhotoClick(initialPhotoUrl)
   }

   override fun cropPhoto(photoPath: Uri) {
      cropImageService.cropImage(activity, photoPath)
   }

   override fun observeCropper(): Observable<File> = cropImageService.observeCropper()

   override fun setDoneButtonEnabled(enable: Boolean) {
      actionDoneMenuItem.isEnabled = enable
   }

   override fun observeChangesProfileFields() = observeProfileViewModel.asObservable()!!

   override fun onDetach(view: View) {
      if (scNonConnectionDialog != null) {
         scNonConnectionDialog!!.dismiss()
      }
      super.onDetach(view)
   }

   override fun getPresenter() = screenPresenter

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         layoutInflater.inflate(R.layout.screen_wallet_settings_profile, viewGroup, false)!!

   override fun supportConnectionStatusLabel() = true

   override fun supportHttpConnectionStatusLabel() = true

   override fun screenModule(): Any? = WalletSettingsProfileScreenModule()
}

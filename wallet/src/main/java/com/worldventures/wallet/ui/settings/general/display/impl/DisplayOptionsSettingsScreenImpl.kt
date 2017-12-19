package com.worldventures.wallet.ui.settings.general.display.impl

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog
import com.worldventures.wallet.R
import com.worldventures.wallet.databinding.DialogWalletDisplayOptionsEnterUserPhoneBinding
import com.worldventures.wallet.service.WalletCropImageService
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand
import com.worldventures.wallet.service.command.settings.general.display.SaveDisplayTypeCommand
import com.worldventures.wallet.service.command.settings.general.display.exception.MissingUserPhoneException
import com.worldventures.wallet.service.command.settings.general.display.exception.MissingUserPhotoException
import com.worldventures.wallet.service.profile.RetryHttpUploadUpdatingCommand
import com.worldventures.wallet.service.profile.UpdateSmartCardUserCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.DialogErrorView
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.RetryDialogErrorView
import com.worldventures.wallet.ui.common.helper2.error.RetryDialogErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.common.helper2.success.SimpleToastSuccessView
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsScreen
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.settings.general.profile.common.UpdateSmartCardUserOperationView
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.ErrorView
import io.techery.janet.operationsubscriber.view.OperationView
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction
import io.techery.janet.smartcard.exception.NotConnectedException
import me.relex.circleindicator.CircleIndicator
import rx.Observable
import java.io.File
import javax.inject.Inject

private const val PARAM_KEY_PROFILE_VIEW_MODEL = "key_profile_view_model"
private const val PARAM_KEY_DISPLAY_OPTIONS_SOURCE = "key_smart_card_user"

private const val STATE_KEY_SELECTED_DISPLAY_TYPE = "DisplayOptionsSettingsScreenImpl#STATE_KEY_SELECTED_DISPLAY_TYPE"
private const val STATE_KEY_DISPLAY_ORIGIN_MODEL = "DisplayOptionsSettingsScreenImpl#STATE_KEY_DISPLAY_ORIGIN_MODEL"
private const val STATE_KEY_DISPLAY_MODEL = "DisplayOptionsSettingsScreenImpl#STATE_KEY_DISPLAY_MODEL"

@Suppress("UnsafeCallOnNullableType")
class DisplayOptionsSettingsScreenImpl(args: Bundle)
   : WalletBaseController<DisplayOptionsSettingsScreen, DisplayOptionsSettingsPresenter>(args),
      DisplayOptionsSettingsScreen, DisplayOptionsClickListener {

   private lateinit var wrapperPager: ViewGroup
   private lateinit var viewPager: ViewPager
   private lateinit var indicator: CircleIndicator

   @Inject lateinit var screenPresenter: DisplayOptionsSettingsPresenter

   private lateinit var cropImageService: WalletCropImageService

   private var originProfileModel: ProfileViewModel? = null
   private var profileModel: ProfileViewModel? = null

   private val profileFromArgs: ProfileViewModel?
      get() = if (args.containsKey(PARAM_KEY_PROFILE_VIEW_MODEL))
         args.getParcelable(PARAM_KEY_PROFILE_VIEW_MODEL)
      else
         null

   override val displayOptionsSource: DisplayOptionsSource
      get() = args.getSerializable(PARAM_KEY_DISPLAY_OPTIONS_SOURCE) as DisplayOptionsSource

   override val isProfileChanged: Boolean
      get() = originProfileModel != profileModel

   override val isProfileBind: Boolean
      get() = originProfileModel != null

   override val profile: ProfileViewModel
      get() = profileModel!!

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View =
         layoutInflater.inflate(R.layout.screen_wallet_settings_display_options, viewGroup, false)

   override fun onAttach(view: View) {
      super.onAttach(view)
      observeNewAvatar()
   }

   override fun supportConnectionStatusLabel(): Boolean = true

   override fun supportHttpConnectionStatusLabel(): Boolean = false

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      setupToolbar(view)
      initViewPager(view)

      cropImageService = context.getSystemService(WalletCropImageService.SERVICE_NAME) as WalletCropImageService
   }

   private fun setupToolbar(view: View) {
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.setNavigationOnClickListener { presenter.goBack() }
      toolbar.inflateMenu(R.menu.wallet_settings_display_options)
      toolbar.setOnMenuItemClickListener callback@ { item ->
         if (item.itemId == R.id.done) {
            saveCurrentChoice()
            return@callback true
         }
         return@callback false
      }
   }

   @Suppress("MagicNumber", "UnsafeCast")
   private fun initViewPager(view: View) {
      viewPager = view.findViewById(R.id.pager)
      wrapperPager = view.findViewById(R.id.wrapper_pager)
      indicator = view.findViewById(R.id.indicator)
      val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
      val screenSize = Point()
      display.getSize(screenSize)

      val screenWidth = screenSize.x
      val pageActualWidth = resources!!.getDimensionPixelSize(R.dimen.wallet_settings_display_options_page_size)
      val marginBetweenPages = resources!!.getDimensionPixelSize(R.dimen.wallet_settings_display_options_page_margin)

      // Use negative margin to show multiple pages on the screen
      val actualPageMargin = screenWidth - pageActualWidth - marginBetweenPages
      viewPager.pageMargin = -actualPageMargin

      // So page begin to appear at 1/3 of page width
      val alphaThreshold = 1 / 3f
      // Calculate position for alpha start changing
      val alphaOffset = (1 - actualPageMargin / screenWidth.toFloat()) * alphaThreshold
      viewPager.setPageTransformer(false) { page, position ->
         (page.tag as DisplayOptionsViewHolder).onPagePositionUpdated(position, alphaOffset)
      }

      viewPager.offscreenPageLimit = DisplayOptionsPagerAdapter.DISPLAY_OPTIONS.size
      viewPager.overScrollMode = OVER_SCROLL_NEVER
   }

   private fun saveCurrentChoice() {
      presenter.saveDisplayType(DisplayOptionsPagerAdapter.DISPLAY_OPTIONS[viewPager.currentItem])
   }

   override fun onAddPhoto() {
      presenter.choosePhoto()
   }

   override fun onAddPhone() {
      showAddPhoneDialog()
   }

   override fun setupDisplayOptions(displayModel: ProfileViewModel, @SetHomeDisplayTypeAction.HomeDisplayType type: Int) {
      setupDisplayOptions(displayModel, profileFromArgs ?: displayModel.copy(), type)
   }

   @Suppress("MagicNumber")
   private fun setupDisplayOptions(originProfile: ProfileViewModel, profileModel: ProfileViewModel, @SetHomeDisplayTypeAction.HomeDisplayType type: Int) {
      this.originProfileModel = originProfile
      this.profileModel = profileModel
      viewPager.adapter = DisplayOptionsPagerAdapter(context, profileModel, this)
      viewPager.currentItem = DisplayOptionsPagerAdapter.DISPLAY_OPTIONS.indexOf(type)
      indicator.setViewPager(viewPager)

      wrapperPager.animate().alpha(1f).duration = 400
   }

   override fun provideGetDisplayTypeOperationView(): OperationView<GetDisplayTypeCommand> {
      return ComposableOperationView(SimpleDialogProgressView(context, R.string.wallet_settings_general_display_loading, false),
            ErrorViewFactory.builder<GetDisplayTypeCommand>()
                  .addProvider(SmartCardErrorViewProvider(context,
                        { presenter.fetchDisplayType() }) { presenter.goBack() })
                  .addProvider(RetryDialogErrorViewProvider(context, NotConnectedException::class.java, R.string.wallet_smart_card_is_disconnected,
                        { presenter.fetchDisplayType() }) { presenter.goBack() })
                  .defaultErrorView(RetryDialogErrorView(context, R.string.wallet_error_something_went_wrong,
                        { presenter.fetchDisplayType() }) { presenter.goBack() })
                  .build()
      )
   }

   override fun provideSaveDisplayTypeOperationView(): OperationView<SaveDisplayTypeCommand> {
      return ComposableOperationView(SimpleDialogProgressView(context, R.string.wallet_settings_general_display_updating, false),
            SimpleToastSuccessView(context, R.string.wallet_settings_general_display_changes_saved),
            ErrorViewFactory.builder<SaveDisplayTypeCommand>()
                  .addProvider(getUserRequiredInfoMissingDialogProvider(MissingUserPhoneException::class.java,
                        R.string.wallet_settings_general_display_phone_required_title,
                        R.string.wallet_settings_general_display_phone_required_message))
                  .addProvider(getUserRequiredInfoMissingDialogProvider(MissingUserPhotoException::class.java,
                        R.string.wallet_settings_general_display_photo_required_title,
                        R.string.wallet_settings_general_display_photo_required_message))
                  .addProvider(SCConnectionErrorViewProvider(context))
                  .addProvider(SmartCardErrorViewProvider(context) { saveCurrentChoice() })
                  .defaultErrorView(RetryDialogErrorView(context, R.string.wallet_error_something_went_wrong) { saveCurrentChoice() })
                  .build()
      )
   }

   override fun showAddPhoneDialog() {
      val phoneView = View.inflate(context, R.layout.dialog_wallet_display_options_enter_user_phone, null)
      val phoneBinding = DataBindingUtil.bind<DialogWalletDisplayOptionsEnterUserPhoneBinding>(phoneView)
      phoneBinding.profile = ProfileViewModel()
      val builder = MaterialDialog.Builder(context)
            .title(R.string.wallet_settings_general_display_photo_add_phone_label)
            .customView(phoneView, false)
            .negativeText(R.string.wallet_cancel_label)
            .onNegative { dialog, _ -> dialog.cancel() }
            .positiveText(R.string.wallet_done_label)
            .onPositive { _, _ -> presenter.phoneNumberEntered(phoneBinding.profile!!.phoneCode, phoneBinding.profile!!.phoneNumber) }
            .build()
      builder.setOnShowListener {
         (phoneView.findViewById<EditText>(R.id.et_phone_number)).setHint(R.string.wallet_settings_general_display_add_phone_number_hint)
         val countryCode = phoneView.findViewById<EditText>(R.id.et_country_code)
         countryCode.setSelection(countryCode.text.length)
      }
      builder.show()
   }

   override fun pickPhoto(initialPhotoUrl: String?) {
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

   override fun cropPhoto(photoPath: Uri) {
      cropImageService.cropImage(activity, photoPath)
   }

   override fun observeCropper(): Observable<File> = cropImageService.observeCropper()

   override fun dropPhoto() { /*nothing*/
   }

   override fun showDialog() { /*nothing*/
   }

   override fun hideDialog() { /*nothing*/
   }

   private fun observeNewAvatar() {
      observeCropper()
            .compose(bindUntilDetach())
            .subscribe { file -> presenter.avatarSelected(Uri.fromFile(file).toString()) }
   }

   override fun updatePhone(phoneCode: String, phoneNumber: String) {
      profileModel?.phoneCode = phoneCode
      profileModel?.phoneNumber = phoneNumber
      viewPager.adapter?.notifyDataSetChanged()
   }

   override fun updatePhoto(photo: String) {
      profileModel?.chosenPhotoUri = photo
      viewPager.adapter?.notifyDataSetChanged()
   }

   override fun provideUpdateSmartCardOperation(delegate: WalletProfileDelegate): OperationView<UpdateSmartCardUserCommand> =
         UpdateSmartCardUserOperationView.UpdateUser(context, delegate, null)

   override fun provideHttpUploadOperation(delegate: WalletProfileDelegate): OperationView<RetryHttpUploadUpdatingCommand> =
         UpdateSmartCardUserOperationView.RetryHttpUpload(context, delegate)

   private fun getUserRequiredInfoMissingDialogProvider(error: Class<out Throwable>, @StringRes title: Int, @StringRes message: Int):
         ErrorViewProvider<SaveDisplayTypeCommand> {
      return object : ErrorViewProvider<SaveDisplayTypeCommand> {
         override fun forThrowable(): Class<out Throwable> = error

         override fun create(command: SaveDisplayTypeCommand, parentThrowable: Throwable?, throwable: Throwable): ErrorView<SaveDisplayTypeCommand> {
            return object : DialogErrorView<SaveDisplayTypeCommand>(context) {
               override fun createDialog(command: SaveDisplayTypeCommand, throwable: Throwable, context: Context): MaterialDialog {
                  return MaterialDialog.Builder(getContext())
                        .title(title)
                        .content(message)
                        .positiveText(R.string.wallet_ok)
                        .build()
               }
            }
         }
      }
   }

   override fun getPresenter() = screenPresenter

   override fun onSaveViewState(view: View, outState: Bundle) {
      outState.putInt(STATE_KEY_SELECTED_DISPLAY_TYPE, DisplayOptionsPagerAdapter.DISPLAY_OPTIONS[viewPager.currentItem])
      outState.putParcelable(STATE_KEY_DISPLAY_MODEL, profileModel)
      outState.putParcelable(STATE_KEY_DISPLAY_ORIGIN_MODEL, originProfileModel)
      super.onSaveViewState(view, outState)
   }

   override fun onRestoreViewState(view: View, savedViewState: Bundle) {
      setupDisplayOptions(
            originProfile = savedViewState.getParcelable(STATE_KEY_DISPLAY_ORIGIN_MODEL),
            profileModel = savedViewState.getParcelable(STATE_KEY_DISPLAY_MODEL),
            type = savedViewState.getInt(STATE_KEY_SELECTED_DISPLAY_TYPE))
      super.onRestoreViewState(view, savedViewState)
   }

   companion object {

      fun create(source: DisplayOptionsSource): DisplayOptionsSettingsScreenImpl = create(null, source)

      fun create(profileViewModel: ProfileViewModel?, source: DisplayOptionsSource): DisplayOptionsSettingsScreenImpl {
         val args = Bundle()
         if (profileViewModel != null) {
            args.putParcelable(PARAM_KEY_PROFILE_VIEW_MODEL, profileViewModel)
         }
         args.putSerializable(PARAM_KEY_DISPLAY_OPTIONS_SOURCE, source)
         return DisplayOptionsSettingsScreenImpl(args)
      }
   }
}

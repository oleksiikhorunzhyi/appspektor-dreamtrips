package com.worldventures.wallet.ui.settings.general.profile

import com.worldventures.wallet.ui.common.base.screen.LifecycleHolder
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.settings.general.profile.common.UpdateSmartCardUserView
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfilePhotoView

import rx.Observable

interface WalletSettingsProfileScreen : WalletScreen, WalletProfilePhotoView, UpdateSmartCardUserView, LifecycleHolder {

   val isDataChanged: Boolean

   var profile: ProfileViewModel

   fun discardChanges()

   fun showRevertChangesDialog()

   fun setDoneButtonEnabled(enable: Boolean)

   fun observeChangesProfileFields(): Observable<ProfileViewModel>

   fun showSCNonConnectionDialog()
}

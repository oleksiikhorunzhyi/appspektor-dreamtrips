package com.worldventures.wallet.ui.settings.general.profile.common

import android.net.Uri

import java.io.File

import rx.Observable

interface WalletProfilePhotoView {

   fun pickPhoto(initialPhotoUrl: String?)

   fun cropPhoto(photoPath: Uri)

   fun observeCropper(): Observable<File>

   fun dropPhoto()

   fun showDialog()

   fun hideDialog()
}

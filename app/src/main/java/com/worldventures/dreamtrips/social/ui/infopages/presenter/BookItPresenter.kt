package com.worldventures.dreamtrips.social.ui.infopages.presenter

import android.os.Build
import com.worldventures.dreamtrips.BuildConfig

class BookItPresenter(val link: String) : WebViewFragmentPresenter<WebViewFragmentPresenter.View>() {

   override fun initUrl() = link

   override fun getAdditionalHeaders() = mapOf(AUTHORIZATION_HEADER_KEY to getAuthToken(), BOOK_IT_HEADER_KEY to BOOK_IT_HEADER)

   companion object {
      private const val BOOK_IT_HEADER_KEY = "DT-Device-Identifier"
      private val BOOK_IT_HEADER = ("Android - ${Build.VERSION.RELEASE} - ${BuildConfig.versionMajor} ."
            + " ${BuildConfig.versionMinor} . ${BuildConfig.versionPatch}")
   }
}

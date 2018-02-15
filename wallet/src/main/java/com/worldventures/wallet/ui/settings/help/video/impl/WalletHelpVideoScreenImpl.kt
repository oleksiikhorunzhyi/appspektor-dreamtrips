package com.worldventures.wallet.ui.settings.help.video.impl

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.view.SimpleDraweeView
import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand
import com.worldventures.core.ui.view.custom.EmptyRecyclerView
import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.R
import com.worldventures.wallet.ui.common.adapter.MultiHolderAdapter
import com.worldventures.wallet.ui.common.adapter.SimpleMultiHolderAdapter
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoPresenter
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoScreen
import com.worldventures.wallet.ui.settings.help.video.delegate.WalletVideoCallback
import com.worldventures.wallet.ui.settings.help.video.holder.VideoHolderFactoryImpl
import com.worldventures.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate
import com.worldventures.wallet.ui.settings.help.video.impl.language.HelpVideoLanguagePicker
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import java.util.ArrayList
import javax.inject.Inject

private const val STATE_KEY_LOCALE = "WalletHelpVideoScreenImpl#STATE_KEY_LOCALE"
private const val STATE_KEY_VIDEOS = "WalletHelpVideoScreenImpl#STATE_KEY_VIDEOS"
private const val STATE_KEY_VIDEO_LOCALES = "WalletHelpVideoScreenImpl#STATE_KEY_VIDEO_LOCALES"

class WalletHelpVideoScreenImpl : WalletBaseController<WalletHelpVideoScreen, WalletHelpVideoPresenter>(), WalletHelpVideoScreen {

   @Inject internal lateinit var screenPresenter: WalletHelpVideoPresenter
   @Inject internal lateinit var videoHolderDelegate: WalletVideoHolderDelegate
   @Inject internal lateinit var httpErrorHandlingUtil: HttpErrorHandlingUtil

   private lateinit var rvVideos: EmptyRecyclerView
   private lateinit var refreshLayout: SwipeRefreshLayout
   private lateinit var videoAdapter: MultiHolderAdapter<WalletVideoModel>

   private lateinit var tvLocaleTitle: TextView
   private lateinit var ivLocaleFlag: SimpleDraweeView
   private lateinit var viewLocaleContainer: View
   private lateinit var languagePicker: HelpVideoLanguagePicker

   private var videoLocale: HelpVideoLocale? = null
      set(value) {
         value?.videoLocale?.let { bindSelectedLocale(it) }
         field = value
      }

   private var videoLocales: ArrayList<VideoLocale>? = null

   override var videos: ArrayList<WalletVideoModel> = ArrayList()
      set(value) {
         field = value
         this.videoAdapter.clear()
         this.videoAdapter.addItems(value)
      }

   private val videoActionsCallback = object : WalletVideoCallback {

      override fun onDownloadMedia(video: WalletVideoModel) {
         presenter.downloadVideo(video.video.cacheEntity)
      }

      override fun onDeleteMedia(video: WalletVideoModel) {
         presenter.deleteCachedVideo(video.video.cacheEntity)
      }

      override fun onCancelCachingMedia(video: WalletVideoModel) {
         presenter.cancelCachingVideo(video.video.cacheEntity)
      }

      override fun onPlayVideoClicked(entity: WalletVideoModel) {
         presenter.onPlayVideo(entity)
      }
   }

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val tvVideosEmpty: TextView = view.findViewById(R.id.tv_video_coming_soon)
      rvVideos = view.findViewById(R.id.rv_videos)
      rvVideos.setEmptyView(tvVideosEmpty)
      initRefreshLayout(view)
      initAdapter()
      initToolbar(view)
   }

   override fun onAttach(view: View) {
      super.onAttach(view)
      if (videoLocales == null) {
         viewLocaleContainer.visibility = View.GONE
         presenter.fetchLocales()
      }
   }

   private fun initRefreshLayout(view: View) {
      refreshLayout = view.findViewById(R.id.refresh_layout)
      refreshLayout.setOnRefreshListener { fetchVideo() }
   }

   private fun initToolbar(view: View) {
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.setNavigationOnClickListener { presenter.goBack() }

      ivLocaleFlag = view.findViewById(R.id.iv_locale_flag)
      tvLocaleTitle = view.findViewById(R.id.tv_locale_title)
      viewLocaleContainer = toolbar.findViewById(R.id.tv_locale_container)
      viewLocaleContainer.setOnClickListener {
         languagePicker.showLocalePicker(it)
      }
      languagePicker = HelpVideoLanguagePicker(context) {
         onLanguageSelected(it)
      }
   }

   private fun onLanguageSelected(videoLocale: HelpVideoLocale?) {
      fetchVideo(videoLocale)
      this.videoLocale = videoLocale
   }

   private fun bindSelectedLocale(locale: VideoLocale) {
      tvLocaleTitle.text = locale.title
      ivLocaleFlag.setImageURI(locale.image)
      viewLocaleContainer.visibility = View.VISIBLE
   }

   private fun initAdapter() {
      videoAdapter = SimpleMultiHolderAdapter(ArrayList(), VideoHolderFactoryImpl(videoActionsCallback, videoHolderDelegate))
      rvVideos.adapter = videoAdapter
   }

   override fun setVideoLocales(videoLocales: ArrayList<VideoLocale>, defaultLocale: HelpVideoLocale) {
      setVideoLocales(videoLocales)
      if (videoLocale == null) {
         onLanguageSelected(defaultLocale)
      }
   }

   private fun setVideoLocales(videoLocales: ArrayList<VideoLocale>) {
      this.videoLocales = videoLocales
      languagePicker.addLocales(videoLocales)
   }

   override fun provideOperationLoadVideos(): OperationView<GetMemberVideosCommand> {
      return ComposableOperationView(
            SwipeRefreshProgressView(refreshLayout),
            ErrorViewFactory.builder<GetMemberVideosCommand>()
                  .addProvider(HttpErrorViewProvider(context, httpErrorHandlingUtil,
                        { fetchVideo() },
                        {
                           val locales = videoLocales
                           if (locales == null || locales.isEmpty()) {
                              presenter.goBack()
                           }
                        })
                  ).build()
      )
   }

   private fun fetchVideo(videoLocale: HelpVideoLocale? = this.videoLocale) {
      videoLocale?.let { presenter.fetchVideos(it) }
   }

   override fun provideOperationLoadLanguages(): OperationView<GetVideoLocalesCommand> {
      return ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_settings_help_video_locales_loading, false),
            ErrorViewFactory.builder<GetVideoLocalesCommand>()
                  .addProvider(HttpErrorViewProvider(context, httpErrorHandlingUtil,
                        { presenter.fetchLocales() }, { presenter.goBack() })
                  ).build()
      )
   }

   override fun confirmCancelDownload(entity: CachedModel) {
      MaterialDialog.Builder(context)
            .content(R.string.wallet_settings_help_video_cancel_cached_video_text)
            .positiveText(R.string.wallet_label_yes)
            .onPositive { _, _ -> presenter.onCancelAction(entity) }
            .negativeText(R.string.wallet_label_no)
            .onNegative { dialog, _ -> dialog.dismiss() }
            .build()
            .show()
   }

   override fun confirmDeleteVideo(entity: CachedModel) {
      MaterialDialog.Builder(context)
            .content(R.string.wallet_settings_help_video_delete_cached_video_text)
            .positiveText(R.string.wallet_label_delete)
            .onPositive { _, _ -> presenter.onDeleteAction(entity) }
            .negativeText(R.string.wallet_label_no)
            .onNegative { dialog, _ -> dialog.dismiss() }
            .build()
            .show()
   }

   override fun notifyItemChanged(cachedEntity: CachedModel) {
//      todo optimize it
      videoAdapter.notifyDataSetChanged()
   }

   override fun getPresenter(): WalletHelpVideoPresenter = screenPresenter

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View =
         layoutInflater.inflate(R.layout.screen_wallet_settings_help_video, viewGroup, false)

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   override fun onSaveViewState(view: View, outState: Bundle) {
      super.onSaveViewState(view, outState)
      outState.putParcelable(STATE_KEY_LOCALE, videoLocale)
      outState.putParcelableArrayList(STATE_KEY_VIDEOS, videos)
      outState.putSerializable(STATE_KEY_VIDEO_LOCALES, videoLocales)
   }

   @Suppress("UNCHECKED_CAST", "UnsafeCast")
   override fun onRestoreViewState(view: View, savedViewState: Bundle) {
      super.onRestoreViewState(view, savedViewState)
      videoLocale = savedViewState.getParcelable(STATE_KEY_LOCALE)
      videos = savedViewState.getParcelableArrayList(STATE_KEY_VIDEOS)
      setVideoLocales(savedViewState.getSerializable(STATE_KEY_VIDEO_LOCALES) as ArrayList<VideoLocale>)
   }

   override fun screenModule() = WalletHelpVideoScreenModule()
}

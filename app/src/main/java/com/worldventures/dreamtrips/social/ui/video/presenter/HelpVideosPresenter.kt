package com.worldventures.dreamtrips.social.ui.video.presenter

import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.modules.video.service.MemberVideosInteractor
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand
import com.worldventures.core.utils.LocaleHelper
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewVideosTabAnalyticAction
import com.worldventures.dreamtrips.social.ui.reptools.delegate.LocaleVideoDelegate
import com.worldventures.dreamtrips.social.ui.video.service.VideoHelperInteractor
import com.worldventures.dreamtrips.social.ui.video.service.command.DetermineHeadersCommand
import com.worldventures.dreamtrips.social.ui.video.service.command.HeaderType
import io.techery.janet.helper.ActionStateSubscriber
import timber.log.Timber
import javax.inject.Inject

open class HelpVideosPresenter : VideoBasePresenter<HelpVideosPresenter.View>() {

   @Inject lateinit var localVideoHelper: LocaleVideoDelegate
   @Inject lateinit var headerInteractor: VideoHelperInteractor
   @Inject lateinit var memberVideosInteractor: MemberVideosInteractor

   override fun takeView(view: HelpVideosPresenter.View) {
      super.takeView(view)
      subscribeToLocalsPipe()
   }

   override fun onResume() {
      super.onResume()
      loadLocals()
   }

   override fun reload() {
      loadLocals()
   }

   open fun subscribeToLocalsPipe() {
      memberVideosInteractor.videoLocalesPipe
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetVideoLocalesCommand>()
                  .onStart { view.startLoading() }
                  .onSuccess {
                     localVideoHelper.saveVideoLocaleList(it.result)
                     processLocales()
                  }.onFail { command, exception ->
               handleError(command, exception)
               memberVideosInteractor.videoLocalesPipe.clearReplays()
            })
   }

   private fun loadVideos(videoLanguage: VideoLanguage) {
      memberVideosInteractor.memberVideosPipe
            .createObservableResult(GetMemberVideosCommand.forHelpVideos(videoLanguage))
            .flatMap {
               headerInteractor.headerPipe.createObservableResult(DetermineHeadersCommand(HeaderType.HELP, it.result))
            }
            .compose(bindViewToMainComposer())
            .subscribe({
               view.finishLoading()
               view.setItems(it.result)
            }, {
               view.finishLoading()
               Timber.e(it)
            })
   }

   private fun processLocales() {
      val videoLocales = localVideoHelper.fetchVideoLocaleList()
      val videoData = localVideoHelper.fetchLocaleAndLanguage(context, videoLocales)
      view.setLocales(ArrayList(videoLocales), videoData.first)
      loadVideos(videoData.second)
   }

   private fun loadLocals() = memberVideosInteractor.videoLocalesPipe.send(GetVideoLocalesCommand())

   fun onLanguageSelected(videoLocale: VideoLocale, videoLanguage: VideoLanguage) {
      localVideoHelper.saveVideoLocaleAndLanguage(videoLocale, videoLanguage)
      processLocales()
   }

   fun trackView() {
      memberVideosInteractor.videoLocalesPipe
            .observeSuccessWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe {
               val videoData = localVideoHelper.fetchLocaleAndLanguage(context, it.result)
               val language = LocaleHelper.obtainLanguageCode(videoData.second.localeName)
               analyticsInteractor.analyticsActionPipe().send(ViewVideosTabAnalyticAction(language))
            }
   }

   interface View : VideoBasePresenter.View {
      fun setLocales(locales: ArrayList<VideoLocale>, defaultValue: VideoLocale?)

      fun showDialog()

      fun setItems(videos: List<Any>)
   }
}

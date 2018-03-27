package com.worldventures.dreamtrips.social.ui.reptools.presenter

import com.worldventures.core.model.session.Feature
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollRepFragment
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.WVAdvantageFragment
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.InviteFragment
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveEnrolRepViewedAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveTrainingVideosViewedAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ReptoolsInviteShareAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.SuccessStoriesViewedAction
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.SuccessStoryListFragment
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.TrainingVideosFragment

class RepToolsPresenter : Presenter<RepToolsPresenter.View>() {

   private lateinit var screens: List<FragmentItem>

   override fun onInjected() {
      super.onInjected()
      screens = provideScreens()
   }

   override fun takeView(view: View) {
      super.takeView(view)
      view.setScreens(screens)
      subscribeToErrorUpdates()
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   private fun subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe { reportNoConnection() }
   }

   fun provideScreens(): List<FragmentItem> {
      val list = mutableListOf(FragmentItem(TrainingVideosFragment::class.java, context.getString(R.string.training_videos)),
            FragmentItem(EnrollRepFragment::class.java, context.getString(R.string.rep_enrollment)),
            FragmentItem(WVAdvantageFragment::class.java, context.getString(R.string.wv_advantage)),
            FragmentItem(SuccessStoryListFragment::class.java, context.getString(R.string.success_stories)))
      if (showInvite()) list.add(FragmentItem(InviteFragment::class.java, context.getString(R.string.invite_and_share)))
      return list
   }

   fun showInvite() = featureManager.available(Feature.REP_TOOLS) && featureManager.available(Feature.INVITATIONS)

   fun trackState(position: Int) {
      val item = screens[position]
      if (item.fragmentClazz == TrainingVideosFragment::class.java) {
         analyticsInteractor.analyticsActionPipe().send(ApptentiveTrainingVideosViewedAction())
      } else if (item.fragmentClazz == EnrollRepFragment::class.java) {
         analyticsInteractor.analyticsActionPipe().send(ApptentiveEnrolRepViewedAction())
      } else if (item.fragmentClazz == SuccessStoryListFragment::class.java) {
         analyticsInteractor.analyticsActionPipe().send(SuccessStoriesViewedAction())
      } else if (item.fragmentClazz == InviteFragment::class.java) {
         analyticsInteractor.analyticsActionPipe().send(ReptoolsInviteShareAction())
      }
   }

   interface View : Presenter.View {
      fun setScreens(items: List<FragmentItem>)

      fun toggleTabStripVisibility(visible: Boolean)
   }
}

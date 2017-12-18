package com.worldventures.dreamtrips.social.ui.membership.presenter

import com.worldventures.core.model.session.Feature
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollMemberFragment
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollMerchantFragment
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.InviteFragment
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.PodcastsFragment
import com.worldventures.dreamtrips.social.ui.video.view.PresentationVideosFragment
import java.util.ArrayList

class MembershipPresenter : Presenter<MembershipPresenter.View>() {

   override fun takeView(view: View) {
      super.takeView(view)
      view.setScreens(provideScreens())

      subscribeToErrorUpdates()
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   private fun subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose<OfflineErrorCommand>(bindViewToMainComposer<OfflineErrorCommand>())
            .subscribe { reportNoConnection() }
   }

   fun provideScreens(): List<FragmentItem> {
      val screens = ArrayList<FragmentItem>()
      screens.add(FragmentItem(PresentationVideosFragment::class.java, context.getString(R.string.presentations)))
      screens.add(FragmentItem(EnrollMemberFragment::class.java, context.getString(R.string.enroll_member)))
      if (enrollMerchantAvailable()) {
         screens.add(FragmentItem(EnrollMerchantFragment::class.java, context.getString(R.string.dt_local_tools)))
      }
      if (inviteAvailable()) {
         screens.add(FragmentItem(InviteFragment::class.java, context.getString(R.string.invite_and_share)))
      }
      if (podcastsAvailable()) {
         screens.add(FragmentItem(PodcastsFragment::class.java, context.getString(R.string.podcasts)))
      }
      return screens
   }

   private fun enrollMerchantAvailable() = featureManager.available(Feature.REP_SUGGEST_MERCHANT)

   private fun inviteAvailable() = !featureManager.available(Feature.REP_TOOLS)

   private fun podcastsAvailable() = featureManager.available(Feature.MEMBERSHIP)

   interface View : Presenter.View {

      fun setScreens(items: List<FragmentItem>?)
   }
}

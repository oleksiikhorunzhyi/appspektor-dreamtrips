package com.worldventures.dreamtrips.social.ui.membership.presenter

import android.accounts.AccountManager
import android.util.Patterns
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.domain.entity.Contact
import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate
import com.worldventures.dreamtrips.social.domain.entity.InviteType
import com.worldventures.dreamtrips.social.service.InviteShareInteractor
import com.worldventures.dreamtrips.social.service.invites.GetInviteTemplatesCommand
import com.worldventures.dreamtrips.social.service.invites.MembersCommand
import com.worldventures.dreamtrips.social.service.invites.ReadMembersCommand
import com.worldventures.dreamtrips.social.service.invites.selectedContacts
import com.worldventures.dreamtrips.social.service.invites.sortedInviteTemplates
import com.worldventures.dreamtrips.social.ui.membership.bundle.TemplateBundle
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.InviteShareTemplateAction
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class SelectTemplatePresenter : Presenter<SelectTemplatePresenter.View>() {

   @field:Inject lateinit var inviteShareInteractor: InviteShareInteractor

   private val currentUserEmail: String
      get() {
         val accounts = AccountManager.get(context).accounts
         accounts.filter { Patterns.EMAIL_ADDRESS.matcher(it.name).matches() }.forEach { return it.name }
         return ""
      }

   override fun takeView(view: View) {
      super.takeView(view)
      subscribeToInviteTemplatesLoading(view)
      reload()
   }

   private fun subscribeToInviteTemplatesLoading(view: View) {
      inviteShareInteractor.invitesTemplatePipe
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetInviteTemplatesCommand>()
                  .onStart { view.startLoading() }
                  .onSuccess { view.addItems(it.result.sortedInviteTemplates()) }
                  .onFinish { view.finishLoading() }
                  .onFail(this::handleError))
   }

   fun reload() {
      inviteShareInteractor.invitesTemplatePipe.send(GetInviteTemplatesCommand())
   }

   fun onTemplateSelected(inviteTemplate: InviteTemplate) {
      inviteShareInteractor.membersPipe.createObservableResult(ReadMembersCommand())
            .compose<MembersCommand>(bindViewToMainComposer<MembersCommand>())
            .subscribe { contactsLoaded(it.result.selectedContacts(), inviteTemplate) }
   }

   private fun contactsLoaded(contacts: List<Contact>?, inviteTemplate: InviteTemplate) {
      if (contacts != null && contacts.isNotEmpty()) {
         view.openTemplate(TemplateBundle(inviteTemplate, currentUserEmail, contacts[0].name,
               if (contacts[0].emailIsMain) InviteType.EMAIL else InviteType.SMS))
         analyticsInteractor.analyticsActionPipe().send(InviteShareTemplateAction())
      } else {
         view.informUser(R.string.invite_select_first)
      }
   }

   interface View : Presenter.View {
      fun openTemplate(templateBundle: TemplateBundle)

      fun startLoading()

      fun finishLoading()

      fun addItems(inviteTemplates: List<InviteTemplate>)
   }

}


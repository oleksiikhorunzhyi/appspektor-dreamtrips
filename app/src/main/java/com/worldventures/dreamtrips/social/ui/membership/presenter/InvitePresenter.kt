package com.worldventures.dreamtrips.social.ui.membership.presenter

import android.content.Intent
import com.worldventures.core.ui.util.permission.PermissionConstants.READ_PHONE_CONTACTS
import com.worldventures.core.ui.util.permission.PermissionConstants.WRITE_PHONE_CONTACTS
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionSubscriber
import com.worldventures.core.ui.util.permission.PermissionUtils
import com.worldventures.dreamtrips.core.utils.IntentUtils
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.domain.entity.Contact
import com.worldventures.dreamtrips.social.domain.entity.InviteType
import com.worldventures.dreamtrips.social.service.InviteShareInteractor
import com.worldventures.dreamtrips.social.service.invites.AddContactCommand
import com.worldventures.dreamtrips.social.service.invites.DeselectAllContactsCommand
import com.worldventures.dreamtrips.social.service.invites.ReadMembersCommand
import com.worldventures.dreamtrips.social.service.invites.SelectContactCommand
import com.worldventures.dreamtrips.social.service.invites.UpdateContactsCommand
import com.worldventures.dreamtrips.social.service.invites.selectedMemberAddresses
import com.worldventures.dreamtrips.social.ui.membership.bundle.ShareBundle
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.AddContactInviteScreenAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.InviteShareContactsAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.SearchInInviteScreenAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.ViewInviteScreenAction
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent
import icepick.State
import javax.inject.Inject

class InvitePresenter(private val shareBundle: ShareBundle?) : Presenter<InvitePresenter.View>() {

   @Inject lateinit var inviteShareInteractor: InviteShareInteractor
   @Inject lateinit var permissionDispatcher: PermissionDispatcher
   @Inject lateinit var permissionUtils: PermissionUtils

   @JvmField @State var type = InviteType.EMAIL
   @JvmField @State var query = ""

   override fun onViewTaken() {
      super.onViewTaken()
      analyticsInteractor.analyticsActionPipe().send(InviteShareContactsAction())
      subscribeToContactLoading()
      loadContacts()
   }

   private fun subscribeToContactLoading() {
      inviteShareInteractor.membersPipe
            .observeSuccess()
            .map {
               it.result.apply {
                  sortBy { it.name }
                  sortBy { !it.selected }
               }
               it.result.filter { it.name.contains(query, ignoreCase = true) }
            }
            .compose(bindViewToMainComposer())
            .subscribe(this::contactsUpdated)
   }

   private fun contactsUpdated(contacts: List<Contact>) {
      val count = contacts.count { it.selected }
      view.apply {
         setContactsList(contacts.map { it.copy() })
         setSelectedCount(count)
         showNextStepButtonVisibility(count > 0)
      }
   }

   private fun loadContacts(withExplanation: Boolean = true) {
      checkPermissions(READ_PHONE_CONTACTS, withExplanation) {
         inviteShareInteractor.updateContactsPipe.send(UpdateContactsCommand(type))
      }
   }

   private fun checkPermissions(permissions: Array<String>, withExplanation: Boolean, permissionGranted: () -> Unit) {
      permissionDispatcher.requestPermission(permissions, withExplanation)
            .compose(bindView())
            .subscribe(PermissionSubscriber()
                  .onPermissionGrantedAction(permissionGranted)
                  .onPermissionDeniedAction { onPermissionDenied(permissions) }
                  .onPermissionRationaleAction {
                     if (withExplanation) view.showPermissionExplanationText(permissions)
                     else onPermissionDenied(permissions)
                  })
   }

   fun onPermissionDenied(permissions: Array<String>) {
      view.showPermissionDenied(permissions)
   }

   fun onTypeSelected(type: InviteType) {
      this.type = type
      loadContacts()
   }

   fun onQuery(query: String) {
      this.query = query
      inviteShareInteractor.membersPipe.send(ReadMembersCommand())
   }

   fun onExplanationShown(permissions: Array<String>) {
      if (permissionUtils.equals(permissions, READ_PHONE_CONTACTS)) loadContacts() else addContactRequired()
   }

   fun addMember(name: String, email: String, phone: String) {
      inviteShareInteractor.addContactPipe.send(AddContactCommand(name, email, phone, type))
   }

   fun deselectAll() {
      inviteShareInteractor.deseseltAllContactsPipe.send(DeselectAllContactsCommand())
   }

   fun onMemberCellSelected(contact: Contact) {
      inviteShareInteractor.selectContactPipe.send(SelectContactCommand(contact))
   }

   fun track() {
      analyticsInteractor.analyticsActionPipe().send(ViewInviteScreenAction())
   }

   fun onSearchStart() {
      analyticsInteractor.analyticsActionPipe().send(SearchInInviteScreenAction())
   }

   fun addContactRequired() {
      analyticsInteractor.analyticsActionPipe().send(AddContactInviteScreenAction())
      checkPermissions(WRITE_PHONE_CONTACTS, true) { view.showAddContactDialog() }
   }

   fun continueAction() {
      if (shareBundle?.shareLink == null) {
         view.openTemplateView()
      } else {
         inviteShareInteractor.membersPipe.createObservableResult(ReadMembersCommand())
               .map<List<String>>({ it.result.selectedMemberAddresses() })
               .subscribe { addresses ->
                  view.shareLink(if (type === InviteType.EMAIL) {
                     IntentUtils.newEmailIntent("", shareBundle.shareLink, *addresses.toTypedArray())
                  } else {
                     IntentUtils.newSmsIntent(context, shareBundle.shareLink, *addresses.toTypedArray())
                  })
               }
      }
   }

   interface View : Presenter.View, PermissionUIComponent {
      fun setContactsList(contactsList: List<Contact>)

      fun showNextStepButtonVisibility(isVisible: Boolean)

      fun setSelectedCount(count: Int)

      fun openTemplateView()

      fun showAddContactDialog()

      fun shareLink(intent: Intent)
   }
}

package com.worldventures.dreamtrips.social.ui.membership.presenter

import android.content.Intent
import android.text.TextUtils
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.core.utils.IntentUtils
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate
import com.worldventures.dreamtrips.social.domain.entity.InviteType
import com.worldventures.dreamtrips.social.service.InviteShareInteractor
import com.worldventures.dreamtrips.social.service.invites.*
import com.worldventures.dreamtrips.social.ui.membership.bundle.TemplateBundle
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.InviteShareEmailAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.InviteShareSmsAction
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class EditTemplatePresenter(templateBundle: TemplateBundle) : Presenter<EditTemplatePresenter.View>() {

   @field:Inject lateinit var inviteShareInteractor: InviteShareInteractor

   private var template: InviteTemplate
   private val inviteType: InviteType
   private val username: String
   private val from: String
   private var preview = false

   init {
      this.template = templateBundle.inviteTemplate
      this.from = templateBundle.email
      this.inviteType = templateBundle.inviteType
      this.username = templateBundle.name
   }

   override fun onResume() {
      super.onResume()
      view.apply {
         setFrom(from)
         setSubject(template.title)
         setWebViewContent(template.content)
      }
      setContacts()
   }

   private fun setContacts() = inviteShareInteractor.membersPipe.createObservableResult(ReadMembersCommand())
         .map { it.result.selectedMemberAddresses() }
         .subscribe { view.setTo(TextUtils.join(", ", it)) }

   fun previewAction() {
      preview = true
      updatePreview()
   }

   private fun updatePreview() {
      createFilledInviteObservable()
            .subscribe(ActionStateSubscriber<CreateFilledInviteCommand>()
                  .onStart { view.startLoading() }
                  .onSuccess { getFilledInvitationsTemplateSuccess(it.result) }
                  .onFinish { view.finishLoading() }
                  .onFail(this::handleError))
   }

   private fun getFilledInvitationsTemplateSuccess(inviteTemplate: InviteTemplate?) {
      if (inviteTemplate != null) {
         view.setWebViewContent(inviteTemplate.content)
         template = inviteTemplate
         if (preview) {
            preview = false
            view.openPreviewTemplate(UrlBundle(inviteTemplate.link))
         }
      }
   }

   fun shareRequest(message: String) {
      createFilledInviteObservable(message)
            .subscribe(ActionStateSubscriber<CreateFilledInviteCommand>()
                  .onSuccess {
                     template = it.result
                     createInviteSuccess(message)
                  }
                  .onFail(this::handleError))
   }

   private fun createFilledInviteObservable(message: String = "") = inviteShareInteractor.createFilledInvitePipe
         .createObservable(CreateFilledInviteCommand(template.id, message))
         .compose(bindViewToMainComposer())

   private fun createInviteSuccess(message: String) {
      inviteShareInteractor.membersPipe.createObservableResult(ReadMembersCommand())
            .map { it.result.selectedMemberAddresses() }
            .subscribe {
               val addresses = it.toTypedArray()

               view.openShare(if (inviteType === InviteType.EMAIL) {
                  val body = context.getString(R.string.invitation_text_template, username,
                        if (TextUtils.isEmpty(message)) "" else "\n\n" + message + ".", template.link)
                  IntentUtils.newEmailIntent(template.title, body, *addresses)
               } else {
                  IntentUtils.newSmsIntent(context, template.title + " " + template.link, *addresses)
               })

               inviteShareInteractor.deseseltAllContactsPipe.send(DeselectAllContactsCommand())
               inviteShareInteractor.sendInvitesPipe.send(SendInvitesCommand(template.id, it, inviteType))
               analyticsInteractor.analyticsActionPipe()
                     .send(if (inviteType === InviteType.EMAIL) InviteShareEmailAction() else InviteShareSmsAction())
            }
   }

   interface View : Presenter.View {

      fun setFrom(from: String)

      fun setSubject(title: String)

      fun setTo(s: String)

      fun setWebViewContent(content: String?)

      fun startLoading()

      fun finishLoading()

      fun openPreviewTemplate(bundle: UrlBundle)

      fun openShare(intent: Intent)
   }
}

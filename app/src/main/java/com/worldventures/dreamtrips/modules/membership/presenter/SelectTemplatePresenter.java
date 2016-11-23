package com.worldventures.dreamtrips.modules.membership.presenter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Patterns;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.bundle.TemplateBundle;
import com.worldventures.dreamtrips.modules.membership.event.MemberStickyEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.service.InviteShareInteractor;
import com.worldventures.dreamtrips.modules.membership.service.command.GetInviteTemplatesCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class SelectTemplatePresenter extends Presenter<SelectTemplatePresenter.View> {

   @Inject InviteShareInteractor inviteShareInteractor;

   private ArrayList<Member> members = new ArrayList<>();

   @Override
   public void takeView(View view) {
      super.takeView(view);
      fetchSelectedMember();
      subscribeToInviteTemplatesLoading(view);
      reload();
   }

   private void fetchSelectedMember() {
      MemberStickyEvent event = eventBus.getStickyEvent(MemberStickyEvent.class);
      if (event != null && event.getMembers() != null) {
         members.clear();
         members.addAll(event.getMembers());
      }
   }

   private void subscribeToInviteTemplatesLoading(View view) {
      inviteShareInteractor.getInviteTemplatesPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetInviteTemplatesCommand>()
                  .onStart(command -> view.startLoading())
                  .onSuccess(command -> handleResponse(command.getResult()))
                  .onFail(this::onFail));
   }

   public void reload() {
      inviteShareInteractor.getInviteTemplatesPipe().send(new GetInviteTemplatesCommand());
   }

   public void onTemplateSelected(InviteTemplate inviteTemplate) {
      if (members != null && members.size() > 0) {
         inviteTemplate.setFrom(getCurrentUserEmail());
         inviteTemplate.setName(members.get(0).getName());
         inviteTemplate.setTo(members);
         inviteTemplate.setType(members.get(0).isEmailMain() ? InviteTemplate.Type.EMAIL : InviteTemplate.Type.SMS);
         view.openTemplate(new TemplateBundle(inviteTemplate));
         TrackingHelper.inviteShareTemplate(getAccountUserId(), inviteTemplate.getId());
      } else {
         view.informUser(R.string.invite_select_first);
      }
   }

   private void onFail(GetInviteTemplatesCommand command, Throwable e) {
      handleError(command, e);
      view.finishLoading();
   }

   private void handleResponse(List<InviteTemplate> inviteTemplates) {
      view.finishLoading();
      Collections.sort(inviteTemplates, (lhs, rhs) -> lhs.getCategory().compareTo(rhs.getCategory()));
      view.addItems(inviteTemplates);
   }


   private String getCurrentUserEmail() {
      Pattern emailPattern = Patterns.EMAIL_ADDRESS;
      Account[] accounts = AccountManager.get(context).getAccounts();
      for (Account account : accounts) {
         if (emailPattern.matcher(account.name).matches()) {
            return account.name;
         }
      }
      return "";
   }


   public interface View extends Presenter.View {
      void openTemplate(TemplateBundle templateBundle);

      void startLoading();

      void finishLoading();

      void addItems(List<InviteTemplate> inviteTemplates);
   }

}


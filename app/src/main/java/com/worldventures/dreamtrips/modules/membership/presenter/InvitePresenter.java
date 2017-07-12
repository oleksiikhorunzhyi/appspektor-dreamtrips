package com.worldventures.dreamtrips.modules.membership.presenter;

import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.utils.delegate.SearchFocusChangedDelegate;
import com.worldventures.dreamtrips.core.utils.ProjectPhoneNumberUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.delegate.MembersSelectedEventDelegate;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate.Type;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.model.SentInvite;
import com.worldventures.dreamtrips.modules.membership.service.InviteShareInteractor;
import com.worldventures.dreamtrips.modules.membership.service.analytics.AddContactInviteScreenAction;
import com.worldventures.dreamtrips.modules.membership.service.analytics.SearchInInviteScreenAction;
import com.worldventures.dreamtrips.modules.membership.service.analytics.ViewInviteScreenAction;
import com.worldventures.dreamtrips.modules.membership.service.command.GetPhoneContactsCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetSentInvitesCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class InvitePresenter extends Presenter<InvitePresenter.View> {

   @Inject InviteShareInteractor inviteShareInteractor;
   @Inject SearchFocusChangedDelegate searchFocusChangedDelegate;
   @Inject MembersSelectedEventDelegate membersSelectedEventDelegate;
   @Inject AnalyticsInteractor analyticsInteractor;

   @State ArrayList<Member> members = new ArrayList<>();

   private WeakHandler queryHandler = new WeakHandler();

   @Override
   public void takeView(View view) {
      super.takeView(view);
      subscribeToContactLoading();
      subscribeToSentInvitesLoading();
      if (members.isEmpty()) loadMembers();
      else contactsLoaded();
      view.setAdapterComparator(getSelectedComparator());
      reportSelectedMembers();
   }

   @Override
   public void dropView() {
      super.dropView();
      membersSelectedEventDelegate.clearReplays();
   }

   private void subscribeToContactLoading() {
      inviteShareInteractor.getPhoneContactsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPhoneContactsCommand>()
                  .onStart(command -> view.startLoading())
                  .onSuccess(command -> {
                     members.clear();
                     members.addAll(command.getResult());
                     contactsLoaded();
                  }));
   }

   private void subscribeToSentInvitesLoading() {
      inviteShareInteractor.getSentInvitesPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetSentInvitesCommand>()
                  .onSuccess(this::sentInvitesLoaded));
   }

   private void sentInvitesLoaded(GetSentInvitesCommand command) {
      view.finishLoading();
      sortContacts();
      sortSelected();
      linkHistoryWithMembers(command.getResult());
      setMembers();
      openTemplateInView();
      showContinueBtnIfNeed();
      view.setSelectedCount(Queryable.from(members).count(Member::isChecked));
   }

   public void loadMembers() {
      Type type = Type.from(view.getSelectedType());
      inviteShareInteractor.getPhoneContactsPipe().send(new GetPhoneContactsCommand(type));
   }

   private void contactsLoaded() {
      inviteShareInteractor.getSentInvitesPipe().send(new GetSentInvitesCommand());
      TrackingHelper.inviteShareContacts(getAccountUserId());
   }

   private void linkHistoryWithMembers(List<SentInvite> inviteTemplates) {
      for (SentInvite sentInvite : inviteTemplates) {
         for (Member member : members) {
            String contact = sentInvite.getContact();
            if (contact.equals(member.getSubtitle())) {
               member.setSentInvite(sentInvite);
            }
         }
      }
   }

   public void addMember(Member member) {
      addToContactList(member.getName(), member.getPhone(), member.getEmail());
      boolean addToLoadedMembers = false;
      switch (Type.from(view.getSelectedType())) {
         case EMAIL:
            addToLoadedMembers = !TextUtils.isEmpty(member.getEmail().trim());
            if (addToLoadedMembers) member.setEmailIsMain(true);
            break;
         case SMS:
            addToLoadedMembers = !TextUtils.isEmpty(member.getPhone().trim());
            if (addToLoadedMembers) member.setEmailIsMain(false);
            break;
      }
      if (addToLoadedMembers) {
         members.add(member);
         sortContacts();
         setMembers();
      }
   }

   private void addToContactList(String name, String phone, String email) {
      int phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
      int emailType = ContactsContract.CommonDataKinds.Email.TYPE_MOBILE;

      ArrayList<ContentProviderOperation> ops = new ArrayList<>();
      ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountManager.KEY_ACCOUNT_TYPE)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, AccountManager.KEY_ACCOUNT_NAME);
      ops.add(op.build());

      op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
      ops.add(op.build());

      op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType);
      ops.add(op.build());

      op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType);

      op.withYieldAllowed(true);
      ops.add(op.build());

      try {
         context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
      } catch (Exception e) {
         Timber.e(e, "");
      }
   }

   public void searchToggle(boolean hasFocus) {
      searchFocusChangedDelegate.post(hasFocus);
   }

   public void onFilter(String newText) {
      queryHandler.removeCallbacksAndMessages(null);
      queryHandler.postDelayed(() -> {
         String query = null;
         if (view != null) {
            switch (Type.from(view.getSelectedType())) {
               case SMS:
                  if (Patterns.PHONE.matcher(newText).matches()) {
                     query = ProjectPhoneNumberUtils.normalizeNumber(newText);
                  } else {
                     query = newText.toLowerCase();
                  }
                  break;
               default:
                  query = newText.toLowerCase();
            }
            view.setFilter(query);
            updatePositions(query);
         }
      }, 150L);
   }

   public void deselectAll() {
      resetSelected();
      membersSelectedEventDelegate.clearReplays();
      setMembers();
   }

   public void showContinueBtnIfNeed() {
      int count = Queryable.from(members).count(Member::isChecked);
      if (count > 0 && view != null) {
         view.setSelectedCount(count);
         view.showNextStepButtonVisibility(true);
      }
   }

   public void onMemberCellSelected(Member member) {
      boolean isVisible = isVisible();

      reportSelectedMembers();

      view.showNextStepButtonVisibility(isVisible);
      int count = Queryable.from(members).count(Member::isChecked);
      view.setSelectedCount(count);

      int to = member.getOriginalPosition();
      Member lastSelectedMember = Queryable.from(members).lastOrDefault(Member::isChecked);
      int lastSelected = lastSelectedMember != null ? lastSelectedMember.getOriginalPosition() : 0;
      view.move(member, to < lastSelected ? lastSelected : to);
   }

   private void reportSelectedMembers() {
      membersSelectedEventDelegate.post(Queryable.from(members)
            .filter(Member::isChecked).toList());
   }

   public boolean isVisible() {
      return members != null && Queryable.from(members).any(Member::isChecked);
   }

   public void continueAction() {
      view.continueAction2();
   }

   private void openTemplateInView() {
      view.openTemplateView();
   }

   private void setMembers() {
      setMembers(null);
   }

   private void setMembers(String query) {
      updatePositions(query);
      view.setMembers(new ArrayList<>(members));
   }

   private void updatePositions(String query) {
      List<Member> temporaryList = TextUtils.isEmpty(query) ? members : Queryable.from(members)
            .filter(item -> item.containsQuery(query))
            .toList();
      Queryable.from(temporaryList).forEachR((member) -> member.setOriginalPosition(temporaryList.indexOf(member)));
   }

   private void sortContacts() {
      Collections.sort(members, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));
   }

   private void sortSelected() {
      Collections.sort(members, getSelectedComparator());
   }

   private Comparator<Member> getSelectedComparator() {
      return (lhs, rhs) -> {
         if (!lhs.isChecked() && !rhs.isChecked()) return lhs.getName().compareTo(rhs.getName());
         //
         return lhs.isChecked() && !rhs.isChecked() ? -1 : !lhs.isChecked() && rhs.isChecked() ? 1 : 0;
      };
   }

   private void resetSelected() {
      Queryable.from(members).forEachR(m -> m.setIsChecked(false));
      view.setSelectedCount(0);
      view.showNextStepButtonVisibility(false);
   }

   public void track() {
      analyticsInteractor.analyticsActionPipe().send(new ViewInviteScreenAction());
   }

   public void onSearchStart() {
      analyticsInteractor.analyticsActionPipe().send(new SearchInInviteScreenAction());
   }

   public void addContactRequired() {
      analyticsInteractor.analyticsActionPipe().send(new AddContactInviteScreenAction());
   }

   public interface View extends Presenter.View {
      void startLoading();

      void finishLoading();

      int getSelectedType();

      void setMembers(List<Member> memberList);

      void setFilter(String newText);

      void sort(Comparator<Member> comparator);

      void setAdapterComparator(Comparator comparator);

      void showNextStepButtonVisibility(boolean isVisible);

      void setSelectedCount(int count);

      void move(Member member, int to);

      void openTemplateView();

      void continueAction2();
   }
}

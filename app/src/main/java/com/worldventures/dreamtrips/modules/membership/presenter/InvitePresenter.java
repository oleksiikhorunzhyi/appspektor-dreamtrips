package com.worldventures.dreamtrips.modules.membership.presenter;

import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetFilledInvitationTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.api.GetInvitationsQuery;
import com.worldventures.dreamtrips.modules.membership.api.InviteBody;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.membership.api.SendInvitationsQuery;
import com.techery.spares.utils.delegate.SearchFocusChangedDelegate;
import com.worldventures.dreamtrips.modules.membership.event.InvitesSentEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellResendEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberStickyEvent;
import com.worldventures.dreamtrips.modules.membership.model.History;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate.Type;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import timber.log.Timber;

public class InvitePresenter extends Presenter<InvitePresenter.View> {

    @Inject
    SnappyRepository db;
    @Inject
    @ForApplication
    Injector injector;
    WeakHandler queryHandler = new WeakHandler();
    @Inject
    SearchFocusChangedDelegate searchFocusChangedDelegate;

    @State
    ArrayList<Member> members = new ArrayList<>();

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.finishLoading();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (members.isEmpty()) {
            loadMembers();
        } else {
            handleResponse();
        }
        //
        view.setAdapterComparator(getSelectedComparator());
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
    }

    public void loadMembers() {
        view.startLoading();
        Type from = Type.from(view.getSelectedType());
        PhoneContactRequest request = new PhoneContactRequest(from);
        injector.inject(request);
        doRequest(request, members -> {
            InvitePresenter.this.members = members;
            handleResponse();
        });
    }

    private void handleResponse() {
        doRequest(new GetInvitationsQuery(), inviteTemplates -> {
            view.finishLoading();
            sortContacts();
            sortSelected();
            linkHistoryWithMembers(inviteTemplates);
            setMembers();
            openTemplateInView();
            showContinueBtnIfNeed();
            view.setSelectedCount(Queryable.from(members).count(Member::isChecked));
        });

        TrackingHelper.inviteShareContacts(getAccountUserId());
    }

    private void linkHistoryWithMembers(ArrayList<History> inviteTemplates) {
        for (History history : inviteTemplates) {
            for (Member member : members) {
                String contact = history.getContact();
                if (contact.equals(member.getSubtitle())) {
                    member.setHistory(history);
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

    public void addToContactList(String name, String phone, String email) {
        int phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        int emailType = ContactsContract.CommonDataKinds.Email.TYPE_MOBILE;

        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();
        ContentProviderOperation.Builder op =
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountManager.KEY_ACCOUNT_TYPE)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, AccountManager.KEY_ACCOUNT_NAME);
        ops.add(op.build());

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        ops.add(op.build());

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType);
        ops.add(op.build());

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
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
                            query = PhoneNumberUtils.normalizeNumber(newText);
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
        eventBus.removeStickyEvent(MemberStickyEvent.class);
        setMembers();
    }

    public void showContinueBtnIfNeed() {
        int count = Queryable.from(members).count(element -> element.isChecked());
        if (count > 0 && view != null) {
            view.setSelectedCount(count);
            view.showNextStepButtonVisibility(true);
        }
    }

    public void onMemberCellSelected(Member member) {
        boolean isVisible = isVisible();

        eventBus.removeStickyEvent(MemberStickyEvent.class);
        eventBus.postSticky(new MemberStickyEvent(Queryable.from(members).filter(element -> {
            return element.isChecked();
        }).toList()));

        view.showNextStepButtonVisibility(isVisible);
        int count = Queryable.from(members).count(element -> element.isChecked());
        view.setSelectedCount(count);

        int to = member.getOriginalPosition();
        Member lastSelectedMember = Queryable.from(members).lastOrDefault(Member::isChecked);
        int lastSelected = lastSelectedMember != null ? lastSelectedMember.getOriginalPosition() : 0;
        view.move(member, to < lastSelected ? lastSelected : to);
    }

    public boolean isVisible() {
        return members != null && Queryable.from(members).any(Member::isChecked);
    }

    public void onEventMainThread(MemberCellResendEvent event) {
        doResend(event.history, event.userName);
    }

    /**
     * Get configurationStarted-filled template by id, and try to resend
     */
    private void doResend(History history, String username) {
        view.startLoading();
        doRequest(new GetFilledInvitationTemplateQuery(history.getTemplateId()), template -> {
            // open share intent
            Intent intent = null;
            switch (history.getType()) {
                case EMAIL:
                    TrackingHelper.inviteShareAction(TrackingHelper.ACTION_RESEND_EMAIL,
                            template.getId(),
                            template.getTo().size());
                    intent = IntentUtils.newEmailIntent(template.getTitle(),
                            String.format(context.getString(R.string.invitation_text_template),
                                    " " + username,
                                    "",
                                    template.getLink()),
                            history.getContact());
                    break;
                case SMS:
                    TrackingHelper.inviteShareAction(TrackingHelper.ACTION_RESEND_SMS,
                            template.getId(),
                            template.getTo().size());
                    intent = IntentUtils.newSmsIntent(context,
                            template.getTitle() + " " + template.getLink(),
                            history.getContact());
                    break;
            }
            activityRouter.openDefaultShareIntent(intent);
            // notify server
            InviteBody body = new InviteBody();
            body.setContacts(Collections.singletonList(history.getContact()));
            body.setTemplateId(history.getTemplateId());
            body.setType(history.getType());
            doRequest(new SendInvitationsQuery(body), stub -> {
                Timber.i("Invitation sending succeeded");
                view.finishLoading();
                eventBus.post(new InvitesSentEvent());
            });
        });
    }

    public void continueAction() {
        view.continueAction2();
    }

    public void openTemplateInView() {
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
        List<Member> temporaryList = TextUtils.isEmpty(query) ? members
                : Queryable.from(members).filter(item -> item.containsQuery(query)).toList();
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
        TrackingHelper.inviteShare(getAccountUserId());
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

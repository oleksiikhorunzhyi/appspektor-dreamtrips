package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.Share;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetFilledInvitationTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.api.GetInvitationsQuery;
import com.worldventures.dreamtrips.modules.membership.api.InviteBody;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.membership.api.SendInvitationsQuery;
import com.worldventures.dreamtrips.modules.membership.event.InvitesSentEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellResendEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberStickyEvent;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;
import com.worldventures.dreamtrips.modules.membership.model.History;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate.Type;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class InvitePresenter extends Presenter<InvitePresenter.View> {

    @Inject
    SnappyRepository db;
    @Inject
    Injector injector;

    private List<Member> members;
    private List<Member> selectedMembers;

    public InvitePresenter(View view) {
        super(view);
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.finishLoading();
    }

    public void loadMembers() {
        view.startLoading();
        Type from = Type.from(view.getSelectedType());
        PhoneContactRequest request = new PhoneContactRequest(from);
        injector.inject(request);
        doRequest(request, members -> {
            view.finishLoading();
            InvitePresenter.this.members = members;
            sortContacts();
            setMembers();
            resetSelected();
            getInvitations();
        });
    }

    private void getInvitations() {
        view.startLoading();
        doRequest(new GetInvitationsQuery(), inviteTemplates -> {
            view.finishLoading();
            linkHistoryWithMembers(inviteTemplates);
            setMembers();
        });
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
        db.addInviteMember(member);
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

    WeakHandler queryHandler = new WeakHandler();

    public void searchToggle(boolean hasFocus) {
        eventBus.post(new SearchFocusChangedEvent(hasFocus));
    }

    public void onFilter(String newText) {
        queryHandler.removeCallbacksAndMessages(null);
        queryHandler.postDelayed(() -> {
            String query = null;
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
        }, 150L);
    }

    public void deselectAll() {
        resetSelected();
        eventBus.removeStickyEvent(MemberStickyEvent.class);
        setMembers();
    }

    public void searchHiden() {
        if (selectedMembers != null && selectedMembers.size() > 0) {
            view.showContinue();
        }
    }

    public void onEventMainThread(MemberCellSelectedEvent event) {
        boolean isVisible = Queryable.from(members).any(Member::isChecked);
        selectedMembers = Queryable.from(members).filter(Member::isChecked).toList();

        eventBus.removeStickyEvent(MemberStickyEvent.class);
        eventBus.postSticky(new MemberStickyEvent(selectedMembers));

        view.showNextStepButtonVisibility(!view.isTabletLandscape() && isVisible);
        view.setSelectedCount(selectedMembers.size());

        moveItem(event.getFrom(), event.getTo());
    }

    private void moveItem(int from, int to) {
        view.move(from, to);
    }

    public void onEventMainThread(MemberCellResendEvent event) {
        doResend(event.history, event.userName);
    }

    /**
     * Get pre-filled template by id, and try to resend
     */
    private void doResend(History history, String username) {
        view.startLoading();
        doRequest(new GetFilledInvitationTemplateQuery(history.getTemplateId()), template -> {
            // open share intent
            Intent intent = null;
            switch (history.getType()) {
                case EMAIL:
                    intent = Share.newEmailIntent(template.getTitle(),
                            String.format(context.getString(R.string.invitation_text_template),
                                    " " + username,
                                    "",
                                    template.getLink()),
                            history.getContact());
                    break;
                case SMS:
                    intent = Share.newSmsIntent(context,
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
        fragmentCompass.remove(Route.SELECT_INVITE_TEMPLATE.getClazzName());
        if (view.isTabletLandscape()) {
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_templates);
            fragmentCompass.add(Route.SELECT_INVITE_TEMPLATE);
        } else {
            activityRouter.openSelectTemplateActivity();
        }
    }

    private void setMembers() {
        view.setMembers(new ArrayList<>(members));
    }

    private void sortContacts() {
        Collections.sort(members, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));
        Collections.sort(members, (lhs, rhs) -> {
            return lhs.isChecked() && !rhs.isChecked() ? 1 :
                    !lhs.isChecked() && rhs.isChecked() ? -1 :
                            0;
        });
    }

    private void resetSelected() {
        Queryable.from(members).forEachR(m -> m.setIsChecked(false));
        if (selectedMembers != null) selectedMembers.clear();
        view.setSelectedCount(0);
        view.showNextStepButtonVisibility(false);
    }

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        int getSelectedType();

        void setMembers(List<Member> memberList);

        void setFilter(String newText);

        void showNextStepButtonVisibility(boolean isVisible);

        void setSelectedCount(int count);

        void showContinue();

        void move(int from, int to);
    }
}

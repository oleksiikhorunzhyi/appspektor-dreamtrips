package com.worldventures.dreamtrips.modules.membership.presenter;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetInvitationsQuery;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectAllRequestEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberStickyEvent;
import com.worldventures.dreamtrips.modules.membership.model.History;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate.Type;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

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
    public void resume() {
        super.resume();
    }

    public void loadMembers() {
        view.startLoading();
        Type from = Type.from(view.getSelectedType());
        PhoneContactRequest request = new PhoneContactRequest(from);
        injector.inject(request);
        dreamSpiceManager.execute(request, new RequestListener<List<Member>>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                handleError(spiceException);
                view.finishLoading();
            }

            @Override
            public void onRequestSuccess(List<Member> members) {
                view.finishLoading();
                InvitePresenter.this.members = members;
                sortByName();
                setMembers();
                resetSelected();
                getInvitations();
            }
        });
    }

    private void getInvitations() {
        dreamSpiceManager.execute(new GetInvitationsQuery(),
                inviteTemplates -> {
                    linkHistoryWithMembers(inviteTemplates);
                    setMembers();
                },
                spiceException -> {

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
                break;
            case SMS:
                addToLoadedMembers = !TextUtils.isEmpty(member.getPhone().trim());
                break;
        }
        if (addToLoadedMembers) {
            members.add(member);
            sortByName();
            setMembers();
        }
    }

    WeakHandler queryHandler = new WeakHandler();

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

    public void onEventMainThread(MemberCellSelectAllRequestEvent event) {
        Queryable.from(members).forEachR(m -> m.setIsChecked(event.isSelectAll()));
        setMembers();
    }

    public void onEventMainThread(MemberCellSelectedEvent event) {
        boolean isVisible = Queryable.from(members).any(Member::isChecked);
        view.showNextStepButtonVisibility(!view.isTabletLandscape() && isVisible);
        eventBus.removeStickyEvent(MemberStickyEvent.class);
        selectedMembers = Queryable.from(members).filter(Member::isChecked).toList();
        eventBus.postSticky(new MemberStickyEvent(selectedMembers));
        view.setSelectedCount(selectedMembers.size());
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

    private void sortByName() {
        Collections.sort(members, ((lhs, rhs) -> lhs.getName().compareTo(rhs.getName())));
    }

    private void resetSelected() {
        Queryable.from(members).forEachR(m -> m.setIsChecked(false));
        if (selectedMembers != null) selectedMembers.clear();
        view.setSelectedCount(0);
    }

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        int getSelectedType();

        void setMembers(List<Member> memberList);

        void setFilter(String newText);

        void showNextStepButtonVisibility(boolean isVisible);

        void setSelectedCount(int count);
    }
}

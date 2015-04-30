package com.worldventures.dreamtrips.modules.membership.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectAllRequestEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.view.fragment.SelectTemplateFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class InvitePresenter extends Presenter<InvitePresenter.View> {

    @Inject SnappyRepository db;
    @Inject Injector injector;

    private List<Member> members;

    public InvitePresenter(View view) {
        super(view);
    }

    @Override
    public void resume() {
        super.resume();
    }

    public void loadMembers() {
        view.startLoading();
        PhoneContactRequest request = new PhoneContactRequest(view.getSelectedType());
        injector.inject(request);
        dreamSpiceManager.execute(request, new RequestListener<ArrayList<Member>>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.finishLoading();
            }

            @Override
            public void onRequestSuccess(ArrayList<Member> members) {
                view.finishLoading();
                InvitePresenter.this.members = members;
                sortByName();
                setMembers();
            }
        });
    }

    public void addMember(Member member) {
        db.addInviteMember(member);
        //
        boolean addToLoadedMembers = false;
        switch (view.getSelectedType()) {
            case InviteTemplate.EMAIL:
                addToLoadedMembers = !TextUtils.isEmpty(member.getEmail().trim());
                break;
            case InviteTemplate.SMS:
                addToLoadedMembers = !TextUtils.isEmpty(member.getPhone().trim());
                break;

        }
        if (addToLoadedMembers) {
            members.add(member);
            sortByName();
            setMembers();
        }
    }

    public void onEventMainThread(MemberCellSelectAllRequestEvent event) {
        Queryable.from(members).forEachR(m -> m.setIsChecked(event.isSelectAll()));
        setMembers();
    }

    public void onEventMainThread(MemberCellSelectedEvent event) {
        boolean isVisible = Queryable.from(members).any(Member::isChecked);
        view.showNextStepButtonVisibility(isVisible);
    }

    public void continueAction() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SelectTemplateFragment.BUNDLE_TO, getSelectedMembers());
        fragmentCompass.add(Route.SELECT_INVITE_TEMPLATE, bundle);
    }

    private void setMembers() {
        view.setMembers(new ArrayList<>(members));
    }

    private void sortByName() {
        Collections.sort(members, ((lhs, rhs) -> lhs.getName().compareTo(rhs.getName())));
    }

    private ArrayList<Member> getSelectedMembers() {
        return new ArrayList<>(Queryable.from(members).filter(Member::isChecked).toList());
    }

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        @InviteTemplate.Type
        int getSelectedType();

        void setMembers(List<Member> memberList);

        void showNextStepButtonVisibility(boolean isVisible);
    }
}

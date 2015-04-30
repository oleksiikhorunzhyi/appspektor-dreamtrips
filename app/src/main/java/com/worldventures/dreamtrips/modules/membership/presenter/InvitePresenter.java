package com.worldventures.dreamtrips.modules.membership.presenter;

import android.os.Bundle;

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
import java.util.List;

import javax.inject.Inject;

public class InvitePresenter extends Presenter<InvitePresenter.View> {

    @Inject
    SnappyRepository db;

    @Inject
    Injector injector;

    public InvitePresenter(View view) {
        super(view);
    }

    @Override
    public void resume() {
        super.resume();
    }

    public void reload() {
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
                view.addItems(members);
            }
        });
    }

    public void onEventMainThread(MemberCellSelectAllRequestEvent event) {
        for (Member member : view.getItems()) {
            member.setIsChecked(event.isSelectAll());
        }
        view.notifyAdapter();
    }

    public void onEventMainThread(MemberCellSelectedEvent event) {
        boolean isVisible = false;
        for (Member member : view.getItems()) {
            isVisible = member.isChecked();
            if (isVisible) break;
        }
        view.showNextStepButtonVisibility(isVisible);
    }

    public void onMemberAdded(Member member) {
        db.addInviteMember(member);
        view.addItem(member);
    }

    public void continueAction() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SelectTemplateFragment.BUNDLE_TO, getSelectedMembers());
        fragmentCompass.add(Route.SELECT_INVITE_TEMPLATE, bundle);
    }

    private ArrayList<Member> getSelectedMembers() {
        return new ArrayList<>(Queryable.from(view.getItems()).filter(Member::isChecked).toList());
    }

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        @InviteTemplate.Type
        int getSelectedType();

        void addItems(List<Member> memberList);

        void addItem(Member member);

        ArrayList<Member> getItems();

        void notifyAdapter();

        void showNextStepButtonVisibility(boolean isVisible);
    }
}

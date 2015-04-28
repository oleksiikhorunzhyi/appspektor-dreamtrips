package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.Context;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.event.SelectAllEvent;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.request.PhoneContactRequest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class InvitePresenter extends Presenter<InvitePresenter.View> {

    @Inject
    Context context;

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

    public void onEventMainThread(SelectAllEvent event) {
        for (Member member : view.getItems()) {
            member.setIsChecked(event.isSelectAll());
        }
        view.notifyAdapter();
    }

    public void onMemberAdded(Member member) {
        db.addInviteMember(member);
        view.addItem(member);
    }


    public interface View extends Presenter.View {
        void addItems(List<Member> memberList);

        void startLoading();

        void finishLoading();

        @PhoneContactRequest.Type
        int getSelectedType();

        List<Member> getItems();

        void notifyAdapter();

        void addItem(Member member);
    }
}

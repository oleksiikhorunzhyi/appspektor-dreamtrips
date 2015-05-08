package com.worldventures.dreamtrips.modules.membership.presenter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.util.Patterns;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetInvitationsTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.event.MemberStickyEvent;
import com.worldventures.dreamtrips.modules.membership.event.TemplateSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.membership.api.GetInvitationsTemplateQuery.MEMBER;
import static com.worldventures.dreamtrips.modules.membership.api.GetInvitationsTemplateQuery.REP;

public class SelectTemplatePresenter extends Presenter<SelectTemplatePresenter.View> {

    @Inject SessionHolder<UserSession> sessionHolder;
    private ArrayList<Member> members;

    public SelectTemplatePresenter(View view) {
        super(view);
        members = new ArrayList<>();
    }

    @Override
    public void resume() {
        super.resume();
        reload();
    }

    public void onEvent(TemplateSelectedEvent event) {
        Bundle bundle = new Bundle();
        InviteTemplate inviteTemplate = event.getInviteTemplate();
        inviteTemplate.setFrom(getCurrentUserEmail());
        getMembers();
        inviteTemplate.setTo(members);
        inviteTemplate.setType(members.get(0).isEmailMain() ?
                InviteTemplate.Type.EMAIL : InviteTemplate.Type.SMS);
        bundle.putSerializable(EditTemplateFragment.TEMPLATE, inviteTemplate);
        activityRouter.openEditInviteActivity(inviteTemplate);
    }

    public void reload() {
        view.startLoading();
        String type = sessionHolder.get().get().getUser().isRep() ? REP : MEMBER;
        dreamSpiceManager.execute(new GetInvitationsTemplateQuery(type),
                this::handleResponse,
                this::handleFail
        );
    }

    private void getMembers() {
        MemberStickyEvent event = eventBus.getStickyEvent(MemberStickyEvent.class);
        members.clear();
        members.addAll(event.getMembers());
    }


    private void handleFail(SpiceException exception) {
        handleError(exception);
        view.finishLoading();
    }

    private void handleResponse(ArrayList<InviteTemplate> inviteTemplates) {
        view.finishLoading();
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

        void startLoading();

        void finishLoading();

        void addItems(ArrayList<InviteTemplate> inviteTemplates);
    }

}


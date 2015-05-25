package com.worldventures.dreamtrips.modules.membership.presenter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.util.Patterns;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetInvitationsTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.event.MemberStickyEvent;
import com.worldventures.dreamtrips.modules.membership.event.TemplateSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class SelectTemplatePresenter extends Presenter<SelectTemplatePresenter.View> {

    private ArrayList<Member> members = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    public void onEvent(TemplateSelectedEvent event) {
        getMembers();

        if (members != null && members.size() > 0) {
            Bundle bundle = new Bundle();
            InviteTemplate inviteTemplate = event.getInviteTemplate();
            inviteTemplate.setFrom(getCurrentUserEmail());
            inviteTemplate.setName(members.get(0).getName());
            inviteTemplate.setTo(members);
            inviteTemplate.setType(members.get(0).isEmailMain() ?
                    InviteTemplate.Type.EMAIL : InviteTemplate.Type.SMS);
            bundle.putSerializable(EditTemplateFragment.TEMPLATE, inviteTemplate);
            activityRouter.openEditInviteActivity(inviteTemplate);
            TrackingHelper.inviteShareTemplate(getUserId(), inviteTemplate.getId());
        } else {
            view.informUser(R.string.invite_select_first);
        }
    }

    public void reload() {
        view.startLoading();
        doRequest(new GetInvitationsTemplateQuery(),
                this::handleResponse,
                this::handleFail);
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

        void startLoading();

        void finishLoading();

        void addItems(ArrayList<InviteTemplate> inviteTemplates);
    }

}


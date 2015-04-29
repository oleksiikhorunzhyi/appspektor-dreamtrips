package com.worldventures.dreamtrips.modules.membership.presenter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.util.Patterns;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetInvitationsTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.event.TemplateSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SelectTemplatePresenter extends Presenter<SelectTemplatePresenter.View> {
    private ArrayList<Member> members;

    public SelectTemplatePresenter(View view, ArrayList<Member> members) {
        super(view);
        this.members = members;
    }

    @Override
    public void resume() {
        super.resume();
        reload();
    }

    public void onEvent(TemplateSelectedEvent event) {
        Bundle bundle = new Bundle();
        InviteTemplate inviteTemplate = event.getInviteTemplate();
        inviteTemplate.setTo(members);
        inviteTemplate.setFrom(getCurrentUserEmail());
        inviteTemplate.setType(members.get(0).isEmailMain() ? InviteTemplate.EMAIL : InviteTemplate.SMS);
        bundle.putSerializable(EditTemplateFragment.TEMPLATE, inviteTemplate);
        activityRouter.openEditInviteActivity(inviteTemplate);
    }

    private void handleFail(SpiceException e) {
        view.finishLoading();
    }

    private void handleResponse(ArrayList<InviteTemplate> inviteTemplates) {

        view.finishLoading();
        view.addItems(inviteTemplates);
    }

    public void reload() {
        view.startLoading();
        dreamSpiceManager.execute(new GetInvitationsTemplateQuery(), this::handleResponse, this::handleFail);
    }

    public interface View extends Presenter.View {

        void startLoading();

        void finishLoading();

        void addItems(ArrayList<InviteTemplate> inviteTemplates);
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
}


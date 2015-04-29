package com.worldventures.dreamtrips.modules.membership.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetInvitationsTemplate;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import java.util.ArrayList;

public class SelectTemplatePresenter extends Presenter<SelectTemplatePresenter.View> {
    public SelectTemplatePresenter(View view) {
        super(view);
    }


    @Override
    public void resume() {
        super.resume();
        reload();
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
        dreamSpiceManager.execute(new GetInvitationsTemplate(), this::handleResponse, this::handleFail);
    }

    public interface View extends Presenter.View {

        void startLoading();

        void finishLoading();

        void addItems(ArrayList<InviteTemplate> inviteTemplates);
    }
}

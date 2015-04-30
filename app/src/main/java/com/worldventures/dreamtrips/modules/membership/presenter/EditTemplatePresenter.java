package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.utils.Share;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetFilledInvitationsTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.ArrayList;
import java.util.List;

public class EditTemplatePresenter extends Presenter<EditTemplatePresenter.View> {
    public static final String TAG = EditTemplatePresenter.class.getSimpleName();
    private InviteTemplate template;

    public EditTemplatePresenter(View view, InviteTemplate template) {
        super(view);
        this.template = template;
    }


    @Override
    public void resume() {
        super.resume();
        view.setFrom(template.getFrom());
        view.setSubject(template.getTitle());
        List<String> to = getMembersAddress();
        view.setTo(TextUtils.join(", ", to));
        handleNewContent(template);
    }

    public List<String> getMembersAddress() {
        List<String> to = new ArrayList<>();
        for (Member member : template.getTo()) {
            to.add(member.getSubtitle());
        }
        return to;
    }

    public void updatePreview() {
        view.startLoading();
        dreamSpiceManager.execute(new GetFilledInvitationsTemplateQuery(
                        template.getId(),
                        view.getMessage()),
                this::handleNewContent,
                this::handleFail);
    }

    private void handleFail(SpiceException spiceException) {
        view.finishLoading();
        Log.e(TAG, "", spiceException);
    }

    private void handleNewContent(InviteTemplate inviteTemplate) {
        view.finishLoading();
        view.setWebViewContent(inviteTemplate.getContent());
    }

    private String getSubject() {
        return template.getTitle();
    }

    private String getBody() {
        return template.getContent();
    }

    private String getSmsBody() {
        return "Hello world";
    }

    public Intent getShareIntent() {
        int type = template.getType();
        List<String> membersAddress = getMembersAddress();
        String[] addresses = membersAddress.toArray(new String[membersAddress.size()]);
        Intent intent;
        if (type == InviteTemplate.EMAIL) {
            intent = Share.newEmailIntent(addresses, getSubject(), getBody());
        } else {
            intent = Share.newSmsIntent(addresses, getSmsBody());
        }
        return intent;
    }

    public void notifyServer() {
        //TODO
    }

    public interface View extends Presenter.View {

        void setFrom(String from);

        void setSubject(String title);

        void setTo(String s);

        void setWebViewContent(String content);

        String getMessage();

        void startLoading();

        void finishLoading();

    }
}

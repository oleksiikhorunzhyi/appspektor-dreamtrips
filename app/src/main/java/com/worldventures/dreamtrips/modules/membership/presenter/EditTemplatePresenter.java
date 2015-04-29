package com.worldventures.dreamtrips.modules.membership.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
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

    public String getSubject() {
        return template.getTitle();
    }

    public String getBody() {
        return template.getContent();
    }

    public String getSmsBody() {
        return "Hello world";
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

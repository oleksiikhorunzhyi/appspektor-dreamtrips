package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.Share;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.GetFilledInvitationsTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.api.InviteBody;
import com.worldventures.dreamtrips.modules.membership.api.SendInvitationsQuery;
import com.worldventures.dreamtrips.modules.membership.event.InvitesSentEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import org.json.JSONObject;

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
        getFilledInvitationsTemplateSuccess(template);
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
                this::getFilledInvitationsTemplateSuccess,
                this::getFilledInvitationsTemplateFailed);
    }

    private void getFilledInvitationsTemplateFailed(SpiceException spiceException) {
        view.finishLoading();
        Log.e(TAG, "", spiceException);
    }

    private void getFilledInvitationsTemplateSuccess(InviteTemplate inviteTemplate) {
        view.finishLoading();
        template.setLink(inviteTemplate.getLink());
        template.setContent(inviteTemplate.getContent());
        view.setWebViewContent(template.getContent());
    }

    private void sentInvitesFailed(SpiceException spiceException) {
        Log.e(TAG, "", spiceException);
    }

    private void createInviteSuccess(InviteTemplate template) {
        Log.i(TAG, "createInviteSuccess");
        getFilledInvitationsTemplateSuccess(template);
        view.shareAction();
        notifyServer();
    }

    private void createInviteFailed(SpiceException spiceException) {
        Log.e(TAG, "", spiceException);
    }

    private void sentInviteSuccess(JSONObject aVoid) {
        Log.i(TAG, "sentInviteSuccess");
        eventBus.post(new InvitesSentEvent());
    }

    private String getSubject() {
        return template.getTitle();
    }

    private String getBody() {
        return template.getContent();
    }

    private String getSmsBody() {
        return template.getLink();
    }

    public Intent getShareIntent() {
        InviteTemplate.Type type = template.getType();
        List<String> membersAddress = getMembersAddress();
        String[] addresses = membersAddress.toArray(new String[membersAddress.size()]);
        Intent intent;
        if (type == InviteTemplate.Type.EMAIL) {
            intent = Share.newEmailIntent(addresses, getSubject(), getBody());
        } else {
            intent = Share.newSmsIntent(addresses, getSmsBody());
        }
        return intent;
    }

    private void notifyServer() {
        InviteBody body = new InviteBody();
        body.setContacts(getContactAddress());
        body.setTemplateId(template.getId());
        body.setType(template.getType());
        dreamSpiceManager.execute(
                new SendInvitationsQuery(body),
                this::sentInviteSuccess,
                this::sentInvitesFailed
        );
    }

    private List<String> getContactAddress() {
        ArrayList<Member> to = template.getTo();
        List<String> result = new ArrayList<>();
        for (Member member : to) {
            result.add(member.getSubtitle());
        }
        return result;
    }

    public void shareRequest() {
        if (view.getMessage().trim().isEmpty()) {
            view.informUser(context.getString(R.string.error_personal_message_is_empty));
        } else {
            dreamSpiceManager.execute(new GetFilledInvitationsTemplateQuery(template.getId(), view.getMessage()),
                    this::createInviteSuccess,
                    this::createInviteFailed);
        }
    }

    public interface View extends Presenter.View {

        void setFrom(String from);

        void setSubject(String title);

        void setTo(String s);

        void setWebViewContent(String content);

        String getMessage();

        void startLoading();

        void finishLoading();

        void shareAction();
    }
}

package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.Share;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.api.GetFilledInvitationsTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.api.InviteBody;
import com.worldventures.dreamtrips.modules.membership.api.SendInvitationsQuery;
import com.worldventures.dreamtrips.modules.membership.event.InvitesSentEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class EditTemplatePresenter extends Presenter<EditTemplatePresenter.View> {

    private InviteTemplate template;
    private boolean preview = false;

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
        view.setWebViewContent(template.getContent());
    }

    public List<String> getMembersAddress() {
        List<String> to = new ArrayList<>();
        for (Member member : template.getTo()) {
            to.add(member.getSubtitle());
        }
        return to;
    }

    public void previewAction() {
        preview = true;
        updatePreview();
    }

    private void updatePreview() {
        view.startLoading();
        doRequest(new GetFilledInvitationsTemplateQuery(
                        template.getId(),
                        view.getMessage()),
                this::getFilledInvitationsTemplateSuccess,
                this::getFilledInvitationsTemplateFailed);
    }

    private void getFilledInvitationsTemplateFailed(SpiceException spiceException) {
        view.finishLoading();
        Timber.e(spiceException, "");
    }

    private void getFilledInvitationsTemplateSuccess(InviteTemplate inviteTemplate) {
        view.finishLoading();
        template.setLink(inviteTemplate.getLink());
        if (preview) {
            preview = false;
            activityRouter.openPreviewActivity(inviteTemplate.getLink());
        }
    }

    private void sentInvitesFailed(SpiceException spiceException) {
        Timber.e(spiceException, "");
    }

    private void createInviteSuccess(InviteTemplate template) {
        Timber.i("createInviteSuccess");
        getFilledInvitationsTemplateSuccess(template);
        activityRouter.openDefaultShareIntent(getShareIntent());
        notifyServer();
    }

    private void createInviteFailed(SpiceException spiceException) {
        Timber.e(spiceException, "");
    }

    private void sentInviteSuccess(JSONObject aVoid) {
        Timber.i("sentInviteSuccess");
        eventBus.post(new InvitesSentEvent());
    }

    private String getSubject() {
        return template.getTitle();
    }

    private String getBody() {
        return String.format(context.getString(R.string.invitation_text_template),
                getUsername(),
                view.getMessage(),
                template.getLink());
    }

    private String getSmsBody() {
        return template.getLink();
    }

    private String getUsername() {
        return getMembersAddress().size() > 1 ? "" : " " + template.getName();
    }


    public Intent getShareIntent() {
        InviteTemplate.Type type = template.getType();
        List<String> membersAddress = getMembersAddress();
        String[] addresses = membersAddress.toArray(new String[membersAddress.size()]);
        Intent intent;
        if (type == InviteTemplate.Type.EMAIL) {
            intent = Share.newEmailIntent(getSubject(), getBody(), addresses);
        } else {
            intent = Share.newSmsIntent(context, getSmsBody(), addresses);
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
        return Queryable.from(template.getTo()).map(Member::getSubtitle).toList();
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

    }
}

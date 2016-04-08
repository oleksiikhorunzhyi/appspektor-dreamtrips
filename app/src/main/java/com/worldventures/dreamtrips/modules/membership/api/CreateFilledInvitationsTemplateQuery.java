package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

public class CreateFilledInvitationsTemplateQuery extends Query<InviteTemplate> {

    private int id;
    private String message;
    private String photoUrl;

    public CreateFilledInvitationsTemplateQuery(int id, String message, String photoUrl) {
        super(InviteTemplate.class);
        this.id = id;
        this.message = message;
        this.photoUrl = photoUrl;
    }

    @Override
    public InviteTemplate loadDataFromNetwork() throws Exception {
        return getService().createInviteTemplate(id, message, photoUrl);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_create_invitation;
    }
}

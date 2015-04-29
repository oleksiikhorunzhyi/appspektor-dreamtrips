package com.worldventures.dreamtrips.modules.membership.event;

import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

public class TemplateSelectedEvent {

    InviteTemplate inviteTemplate;

    public TemplateSelectedEvent(InviteTemplate inviteTemplate) {
        this.inviteTemplate = inviteTemplate;
    }

    public InviteTemplate getInviteTemplate() {
        return inviteTemplate;
    }
}

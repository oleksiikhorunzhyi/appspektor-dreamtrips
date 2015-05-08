package com.worldventures.dreamtrips.modules.membership.api;

import android.support.annotation.StringDef;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class GetInvitationsTemplateQuery extends Query<ArrayList<InviteTemplate>> {

    private String type;

    @StringDef({ REP, MEMBER})
    @Retention(RetentionPolicy.SOURCE)
    @interface TemplateType {}

    @GetInvitationsTemplateQuery.TemplateType public static final String REP = "InvitationRepTemplate";
    @GetInvitationsTemplateQuery.TemplateType public static final String MEMBER = "InvitationMemberTemplate";

    public GetInvitationsTemplateQuery(@TemplateType String type) {
        super((Class<ArrayList<InviteTemplate>>) new ArrayList<InviteTemplate>().getClass());
        this.type = type;
    }

    @Override
    public ArrayList<InviteTemplate> loadDataFromNetwork() throws Exception {
        return getService().getInviteTemplates(type);
    }
}

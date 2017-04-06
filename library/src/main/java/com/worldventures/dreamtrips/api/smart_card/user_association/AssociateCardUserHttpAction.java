package com.worldventures.dreamtrips.api.smart_card.user_association;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.AssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "api/smartcard/provisioning/card_user", method = POST)
public class AssociateCardUserHttpAction extends AuthorizedHttpAction {

    @Body
    public final AssociationCardUserData associationCardUserData;

    @Response
    SmartCardDetails smartCardDetails;

    public AssociateCardUserHttpAction(AssociationCardUserData associationCardUserData) {
        this.associationCardUserData = associationCardUserData;
    }

    public SmartCardDetails response() {
        return smartCardDetails;
    }
}

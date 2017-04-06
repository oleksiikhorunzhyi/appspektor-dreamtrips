package com.worldventures.dreamtrips.api.smart_card.association_info;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.association_info.model.SmartCardData;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.GET;

@HttpAction(value = "api/smartcard/provisioning/card_data/{scID}", method = GET)
@Deprecated
/**
 * @See com.worldventures.dreamtrips.api.smart_card.association_info.GetAssociatedCardsHttpAction
 */
public class GetCardDataHttpAction extends AuthorizedHttpAction {

    @Path("scID")
    public final long scId;

    @Response
    SmartCardData cardData;

    public GetCardDataHttpAction(long scId) {this.scId = scId;}

    public SmartCardData response() {
        return cardData;
    }
}

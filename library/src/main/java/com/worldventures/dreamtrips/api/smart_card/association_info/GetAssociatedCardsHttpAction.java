package com.worldventures.dreamtrips.api.smart_card.association_info;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.association_info.model.SmartCardInfo;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("api/smartcard/provisioning/device_cards_data")
public class GetAssociatedCardsHttpAction extends AuthorizedHttpAction {

    @Query("device_id")
    public final String deviceId;

    @Response
    List<SmartCardInfo> response;

    public GetAssociatedCardsHttpAction(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<SmartCardInfo> response() {
        return response;
    }
}

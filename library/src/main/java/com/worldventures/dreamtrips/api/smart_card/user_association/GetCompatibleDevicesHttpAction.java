package com.worldventures.dreamtrips.api.smart_card.user_association;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.session.model.Device;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("api/smartcard/provisioning/devices")
public class GetCompatibleDevicesHttpAction extends PaginatedHttpAction {

    @Response
    List<Device> devices;

    public GetCompatibleDevicesHttpAction(int page, int perPage) {
        super(page, perPage);
    }

    public List<Device> response() {
        return devices;
    }

}

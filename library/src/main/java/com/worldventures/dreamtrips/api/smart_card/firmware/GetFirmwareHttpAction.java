package com.worldventures.dreamtrips.api.smart_card.firmware;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("api/smartcard/firmware")
public class GetFirmwareHttpAction extends AuthorizedHttpAction {

    @Query("current_firmware")
    public final String currentFirmwareVersion;

    @Query("current_sdk")
    public final String currentSdkVersion;

    @Response
    FirmwareResponse response;

    public GetFirmwareHttpAction(String currentFirmwareVersion, String currentSdkVersion) {
        this.currentFirmwareVersion = currentFirmwareVersion;
        this.currentSdkVersion = currentSdkVersion;
    }

    public FirmwareResponse response() {
        return response;
    }
}

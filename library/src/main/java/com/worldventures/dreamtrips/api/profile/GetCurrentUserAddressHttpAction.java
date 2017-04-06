package com.worldventures.dreamtrips.api.profile;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.profile.model.AddressType;
import com.worldventures.dreamtrips.api.profile.model.ProfileAddress;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("api/profile/addresses")
public class GetCurrentUserAddressHttpAction extends AuthorizedHttpAction {

    @Query("type")
    public final String type;

    @Response
    List<ProfileAddress> addresses;

    public GetCurrentUserAddressHttpAction() {
        this(null);
    }

    public GetCurrentUserAddressHttpAction(@Nullable AddressType type) {
        this.type = type == null ? null : type.toString().toLowerCase();
    }

    public List<ProfileAddress> response() {
        return addresses;
    }
}

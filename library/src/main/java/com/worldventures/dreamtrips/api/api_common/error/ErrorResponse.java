package com.worldventures.dreamtrips.api.api_common.error;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.HashMap;

@Gson.TypeAdapters
@Value.Immutable
public abstract class ErrorResponse {

    @SerializedName("errors")
    public abstract HashMap<String, String[]> errors();

    public static final String BASIC_ERROR = "base";

    public String reasonFor(String type) {
        if (errors().isEmpty()) return null;
        String[] reasons = errors().get(type);
        return reasons == null || reasons.length == 0 ? null : reasons[0];
    }

    @Value.Derived
    public String reasonForAny() {
        if (errors().values().isEmpty()) return null;
        return errors().values().iterator().next()[0];
    }

}

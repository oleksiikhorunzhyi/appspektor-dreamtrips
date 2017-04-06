package com.worldventures.dreamtrips.api.invitation.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface FilledInvitationParams {

    @Value.Parameter
    @SerializedName("template_id")
    int templateId();

    @Value.Parameter
    @SerializedName("message")
    String message();

}

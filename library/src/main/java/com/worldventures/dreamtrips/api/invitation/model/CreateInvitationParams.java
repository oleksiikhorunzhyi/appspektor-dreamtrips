package com.worldventures.dreamtrips.api.invitation.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface CreateInvitationParams {
    @SerializedName("type")
    InvitationType type();

    @SerializedName("filled_template_id")
    int templateId();

    @SerializedName("contacts")
    List<String> contacts();
}

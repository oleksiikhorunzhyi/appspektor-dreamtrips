package com.worldventures.dreamtrips.api.invitation.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Date;

@Gson.TypeAdapters
@Value.Immutable
public interface Invitation extends Identifiable<Integer> {

    @SerializedName("invitation_filled_template_id")
    int templateId();

    @SerializedName("contact")
    String contact();

    @SerializedName("type")
    InvitationType type();

    @SerializedName("date")
    Date date();

}

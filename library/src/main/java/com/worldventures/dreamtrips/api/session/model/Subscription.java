package com.worldventures.dreamtrips.api.session.model;

import com.google.gson.annotations.SerializedName;

public enum Subscription {
    /** Standard DreamTrips Member */
    @SerializedName("DTM")DTM,
    /** DreamTrips Gold membership */
    @SerializedName("DTG")DTG,
    /** DreamTrips Platinum membership */
    @SerializedName("DTP")DTP,
    /** DTL = DreamTrips Life membership */
    @SerializedName("DTS")DTS,
    /** Representative */
    @SerializedName("RBS")RBS,
    /** Ignoring */
    @SerializedName("LDTM")LDTM,

    UNKNOWN
}

package com.worldventures.dreamtrips.modules.common.model;

public class FlagData {

    public final String uid;
    public final int flagReasonId;
    public final String nameOfReason;

    public FlagData(String uid, int flagReasonId, String nameOfReason) {
        this.uid = uid;
        this.flagReasonId = flagReasonId;
        this.nameOfReason = nameOfReason;
    }
}

package com.worldventures.dreamtrips.modules.tripsimages.api.upload;

public interface TransferListener{
    void onEvent(TransferState state);


    public enum TransferState{
        COMPLETED,
        FAILED,
        WAITING_FOR_NETWORK
    }

}

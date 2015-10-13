package com.worldventures.dreamtrips.modules.dtl.event;

public class DtlMapInfoReadyEvent {

    private int offset;

    public DtlMapInfoReadyEvent(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}

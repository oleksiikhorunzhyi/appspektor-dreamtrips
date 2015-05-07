package com.worldventures.dreamtrips.modules.membership.event;

import com.worldventures.dreamtrips.modules.membership.model.History;

public class MemberCellResendEvent {
    public final History history;

    public MemberCellResendEvent(History history) {
        this.history = history;
    }
}

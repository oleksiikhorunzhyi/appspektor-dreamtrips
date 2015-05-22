package com.worldventures.dreamtrips.modules.membership.event;

import com.worldventures.dreamtrips.modules.membership.model.History;

public class MemberCellResendEvent {
    public final History history;
    public final String userName;

    public MemberCellResendEvent(History history, String userName) {
        this.history = history;
        this.userName = userName;
    }
}

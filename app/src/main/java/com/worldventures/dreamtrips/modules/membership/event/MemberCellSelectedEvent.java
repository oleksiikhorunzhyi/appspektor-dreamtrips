package com.worldventures.dreamtrips.modules.membership.event;

import com.worldventures.dreamtrips.modules.membership.model.Member;

public class MemberCellSelectedEvent {
    private boolean selected;
    private Member member;

    public MemberCellSelectedEvent(Member member) {
        this.member = member;
    }

    public boolean isSelected() {
        return member.isChecked();
    }

    public Member getMember() {
        return member;
    }
}

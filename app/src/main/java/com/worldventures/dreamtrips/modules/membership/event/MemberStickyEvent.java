package com.worldventures.dreamtrips.modules.membership.event;

import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.List;

public class MemberStickyEvent {

   private List<Member> members;

   public MemberStickyEvent(List<Member> members) {
      this.members = members;
   }

   public List<Member> getMembers() {
      return members;
   }

}

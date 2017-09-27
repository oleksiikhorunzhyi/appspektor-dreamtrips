package com.worldventures.dreamtrips.social.ui.membership.delegate;

import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.social.ui.membership.model.Member;

import java.util.List;

public class MembersSelectedEventDelegate extends ReplayEventDelegate<List<Member>> {

   public MembersSelectedEventDelegate(ReplayEventDelegatesWiper wiper) {
      super(wiper);
   }
}

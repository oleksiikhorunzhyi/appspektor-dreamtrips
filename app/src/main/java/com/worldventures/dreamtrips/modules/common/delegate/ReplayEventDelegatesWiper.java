package com.worldventures.dreamtrips.modules.common.delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReplayEventDelegatesWiper {

   private List<ReplayEventDelegate> delegates = Collections.synchronizedList(new ArrayList<>());

   public void register(ReplayEventDelegate replayEventDelegate) {
      delegates.add(replayEventDelegate);
   }

   public void clearReplays() {
      synchronized (delegates) {
         for (ReplayEventDelegate replayEventDelegate : delegates) {
            replayEventDelegate.clearReplays();
         }
      }
   }
}

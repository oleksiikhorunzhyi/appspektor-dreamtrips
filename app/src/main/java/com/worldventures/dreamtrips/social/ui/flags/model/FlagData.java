package com.worldventures.dreamtrips.social.ui.flags.model;

public class FlagData {

   public final String uid;
   public final int flagReasonId;
   public final String reason;

   public FlagData(String uid, int flagReasonId, String reason) {
      this.uid = uid;
      this.flagReasonId = flagReasonId;
      this.reason = reason;
   }
}

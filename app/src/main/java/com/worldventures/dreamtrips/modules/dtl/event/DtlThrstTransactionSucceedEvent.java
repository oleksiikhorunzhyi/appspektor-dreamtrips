package com.worldventures.dreamtrips.modules.dtl.event;

public class DtlThrstTransactionSucceedEvent {
   public final String earnedPoints;
   public final String totalPoints;

   public DtlThrstTransactionSucceedEvent(String earnedPoints, String totalPoints) {
      this.earnedPoints = earnedPoints;
      this.totalPoints = totalPoints;
   }
}

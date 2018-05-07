package com.worldventures.dreamtrips.modules.dtl.domain.storage.snappy;

public interface DtlSnappyRepository {

   boolean shouldShowHowToVideoHint();

   void setShouldShowHowToVideoHint(boolean shouldShow);

   long lastRemindMeLaterTimestamp();

   void setRemindMeLaterTimestamp(long time);
}

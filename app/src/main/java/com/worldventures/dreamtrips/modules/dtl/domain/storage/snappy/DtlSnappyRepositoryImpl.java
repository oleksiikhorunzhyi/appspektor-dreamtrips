package com.worldventures.dreamtrips.modules.dtl.domain.storage.snappy;

import android.content.Context;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.repository.BaseSnappyRepository;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;

public class DtlSnappyRepositoryImpl extends BaseSnappyRepository implements DtlSnappyRepository {

   private static final String PAY_IN_APP_SHOW_HOW_TO_VIDEO = "PAY_IN_APP_SHOW_HOW_TO_VIDEO";
   private static final String PAY_IN_APP_REMIND_LATER_TIMESTAMP = "PAY_IN_APP_REMIND_LATER_TIMESTAMP";

   private final DefaultSnappyOpenHelper defaultSnappyOpenHelper;

   public DtlSnappyRepositoryImpl(Context context, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      super(context, defaultSnappyOpenHelper.provideExecutorService());
      this.defaultSnappyOpenHelper = defaultSnappyOpenHelper;
   }

   @Override
   public boolean shouldShowHowToVideoHint() {
      return actWithResult(db -> db.getBoolean(PAY_IN_APP_SHOW_HOW_TO_VIDEO)).or(true);
   }

   @Override
   public void setShouldShowHowToVideoHint(boolean shouldShow) {
      act(db -> db.put(PAY_IN_APP_SHOW_HOW_TO_VIDEO, shouldShow));
   }

   @Override
   public long lastRemindMeLaterTimestamp() {
      return actWithResult(db -> db.getLong(PAY_IN_APP_REMIND_LATER_TIMESTAMP)).or(0L);
   }

   @Override
   public void setRemindMeLaterTimestamp(long time) {
      act(db -> db.putLong(PAY_IN_APP_REMIND_LATER_TIMESTAMP, time));
   }

   @Nullable
   @Override
   protected DB openDbInstance(Context context) throws SnappydbException {
      return defaultSnappyOpenHelper.openDbInstance(context);
   }
}

package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

import java.util.List;

public interface RecordsStorage extends ModelStorage {

   void saveRecords(List<Record> items);

   @NonNull
   List<Record> readRecords();

   void deleteAllRecords();

   void saveDefaultRecordId(String id);

   @Nullable
   String readDefaultRecordId();

   void deleteDefaultRecordId();

   void saveOfflineModeState(boolean enabled);

   @NonNull
   Boolean readOfflineModeState();

}
package com.worldventures.dreamtrips.wallet.service.command;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.PersistentRecordsStorage;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchDefaultRecordCommand extends Command<Record> implements InjectableAction {

   @Inject PersistentRecordsStorage recordsStorage;

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      List<Record> records = recordsStorage.readRecords();
      String defaultId = recordsStorage.readDefaultRecordId();
      Record defaultRecord = Queryable.from(records).firstOrDefault(c -> TextUtils.equals(c.id(), defaultId));
      callback.onSuccess(defaultRecord);
   }
}

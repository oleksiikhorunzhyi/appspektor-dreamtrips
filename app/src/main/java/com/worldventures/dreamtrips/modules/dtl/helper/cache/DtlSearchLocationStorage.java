package com.worldventures.dreamtrips.modules.dtl.helper.cache;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;

import java.util.ArrayList;
import java.util.List;

public class DtlSearchLocationStorage extends MemoryStorage<Pair<String, List<DtlExternalLocation>>> implements ActionStorage<Pair<String, List<DtlExternalLocation>>> {

   public DtlSearchLocationStorage() {
      save(null, new Pair<>("", new ArrayList<>()));
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DtlSearchLocationAction.class;
   }
}

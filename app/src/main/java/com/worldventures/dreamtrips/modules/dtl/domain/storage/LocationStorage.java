package com.worldventures.dreamtrips.modules.dtl.domain.storage;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.action.SearchLocationAction;

import java.util.ArrayList;
import java.util.List;

public class LocationStorage extends MemoryStorage<Pair<String, List<DtlLocation>>> implements ActionStorage<Pair<String, List<DtlLocation>>> {

   public LocationStorage() {
      save(null, new Pair<>("", new ArrayList<>()));
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return SearchLocationAction.class;
   }
}

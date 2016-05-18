package com.worldventures.dreamtrips.core.janet.cache;

import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.helper.cache.DtlLocationStorage;
import com.worldventures.dreamtrips.modules.dtl.helper.cache.DtlMerchantsStorage;
import com.worldventures.dreamtrips.modules.dtl.helper.cache.DtlSearchLocationStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class CacheActionStorageModule {

    @Singleton
    @Provides(type = Provides.Type.SET)
    ActionStorage provideDtlMerchantsStorage(SnappyRepository db) {
        return new DtlMerchantsStorage(db);
    }

    @Singleton
    @Provides(type = Provides.Type.SET)
    ActionStorage provideDtlSearchLocationStorage() {
        return new DtlSearchLocationStorage();
    }

    @Singleton
    @Provides(type = Provides.Type.SET)
    ActionStorage provideDtlLocationStorage(SnappyRepository db) {
        return new DtlLocationStorage(db);
    }
}

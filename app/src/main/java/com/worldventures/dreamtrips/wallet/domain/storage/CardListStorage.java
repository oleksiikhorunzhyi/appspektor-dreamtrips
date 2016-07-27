package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;

public class CardListStorage implements ActionStorage<Void> {
    @Override
    public Class<? extends CachedAction> getActionClass() {
        return null;
    }

    @Override
    public void save(@Nullable CacheBundle params, Void data) {

    }

    @Override
    public Void get(@Nullable CacheBundle action) {
        return null;
    }
}

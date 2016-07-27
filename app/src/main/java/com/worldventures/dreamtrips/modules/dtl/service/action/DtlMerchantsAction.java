package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.location.Location;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.rx.composer.ListFilter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.PartnerStatus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlMerchantsAction extends Command<List<DtlMerchant>>
        implements CachedAction<List<DtlMerchant>>, InjectableAction {

    @Inject
    Janet janet;

    private List<DtlMerchant> cache = new ArrayList<>();

    private final Location location;

    private final long startTime = System.currentTimeMillis();

    private DtlMerchantsAction(Location location) {
        this.location = location;
    }

    @Override
    protected void run(CommandCallback<List<DtlMerchant>> callback) throws Throwable {
        if (location != null) {
            janet.createPipe(DtlLoadMerchantsAction.class)
                    .createObservableResult(new DtlLoadMerchantsAction(location))
                    .map(DtlLoadMerchantsAction::getResponse)
                    .compose(new ListFilter<>(
                            merchant -> merchant.getPartnerStatus() != PartnerStatus.UNKNOWN,
                            merchant -> merchant.getType() != MerchantType.UNKNOWN))
                    .subscribe(callback::onSuccess, callback::onFail);
        } else {
            callback.onSuccess(cache);
        }
    }

    public boolean isFromApi() {
        return location != null;
    }

    public long getStartTime() {
        return startTime;
    }

    public static DtlMerchantsAction load(Location location) {
        return new DtlMerchantsAction(location);
    }

    public static DtlMerchantsAction restore() {
        return new DtlMerchantsAction(null);
    }

    @Override
    public List<DtlMerchant> getCacheData() {
        return getResult();
    }

    @Override
    public void onRestore(ActionHolder holder, List<DtlMerchant> cache) {
        this.cache = cache;
    }

    @Override
    public CacheOptions getCacheOptions() {
        return ImmutableCacheOptions.builder()
                .restoreFromCache(!isFromApi())
                .saveToCache(isFromApi())
                .build();
    }
}

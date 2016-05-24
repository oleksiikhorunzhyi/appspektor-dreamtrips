package com.worldventures.dreamtrips.modules.dtl.helper.cache;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

public class DtlLocationStorage extends MemoryStorage<DtlLocation> implements ActionStorage<DtlLocation> {

    private final SnappyRepository db;

    public DtlLocationStorage(SnappyRepository db) {
        this.db = db;
    }

    @Override
    public void save(DtlLocation data) {
        super.save(data);
        if (data == null
                || data.getLocationSourceType() == LocationSourceType.UNDEFINED) {
            db.cleanDtlLocation();
        } else {
            db.saveDtlLocation(data);
            db.cleanLastMapCameraPosition();
        }
    }

    @Override
    public DtlLocation get() {
        DtlLocation location = super.get();
        if (location == null) {
            location = db.getDtlLocation();
            super.save(location);
        }
        return location;
    }

    @Override
    public Class<? extends CachedAction> getActionClass() {
        return DtlLocationCommand.class;
    }
}

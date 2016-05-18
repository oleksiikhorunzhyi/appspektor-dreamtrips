package com.worldventures.dreamtrips.modules.dtl.action;

import android.location.Location;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantStoreAction.Action.LOAD;

@CommandAction
public class DtlMerchantStoreAction extends ValueCommandAction<DtlMerchantStoreAction.Action> {

    public enum Action {
        LOAD
    }

    private final Location location;

    private DtlMerchantStoreAction(Action value, Location location) {
        super(value);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public static DtlMerchantStoreAction load(Location location) {
        return new DtlMerchantStoreAction(LOAD, location);
    }
}

package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;

@Layout(R.layout.screen_dtl_details)
public class DtlMerchantDetailsPath extends DtlDetailPath {

    private final String id;

    public DtlMerchantDetailsPath(MasterDetailPath path, @NonNull String id) {
        super(path);
        if (id == null || TextUtils.isEmpty(id))
            throw new IllegalArgumentException("Merchant's \'id\' argument can not be null or empty");
        //
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public PathAttrs getAttrs() {
        return WITHOUT_DRAWER;
    }
}

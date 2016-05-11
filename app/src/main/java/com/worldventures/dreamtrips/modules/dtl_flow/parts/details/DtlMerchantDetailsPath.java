package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jakewharton.rxbinding.internal.Preconditions;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;

@Layout(R.layout.screen_dtl_details)
public class DtlMerchantDetailsPath extends DtlDetailPath {

    private final String id;
    private final DtlOfferData preExpandOffer;

    public DtlMerchantDetailsPath(MasterDetailPath path, @NonNull String id, @Nullable DtlOfferData preExpandOffer) {
        super(path);
        Preconditions.checkArgument(!TextUtils.isEmpty(id), "Merchant's \'id\' argument can not be null or empty");
        this.id = id;
        this.preExpandOffer = preExpandOffer;
    }

    public String getId() {
        return id;
    }

    public DtlOfferData getPreExpandOffer() {
        return preExpandOffer;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }
}

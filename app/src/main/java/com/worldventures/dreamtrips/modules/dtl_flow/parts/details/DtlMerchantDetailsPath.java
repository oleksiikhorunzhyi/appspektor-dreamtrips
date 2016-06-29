package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jakewharton.rxbinding.internal.Preconditions;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import java.util.Collections;
import java.util.List;

import flow.path.Path;

@Layout(R.layout.screen_dtl_details)
public class DtlMerchantDetailsPath extends DtlDetailPath {

    private final DtlMerchant merchant;
    private final List<Integer> preExpandOfferPositions;

    public DtlMerchantDetailsPath(MasterDetailPath path, @NonNull DtlMerchant merchant, @Nullable List<Integer> preExpandOfferPositions) {
        super(path);
        Preconditions.checkNotNull(merchant, "Merchant can not be null");
        this.merchant = merchant;
        this.preExpandOfferPositions = preExpandOfferPositions != null ? preExpandOfferPositions : Collections.emptyList();
    }

    public DtlMerchant getMerchant() {
        return merchant;
    }

    public List<Integer> getPreExpandOffers() {
        return preExpandOfferPositions;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }

    @Override
    public Path getMasterToolbarPath() {
        return MasterToolbarPath.INSTANCE;
    }
}

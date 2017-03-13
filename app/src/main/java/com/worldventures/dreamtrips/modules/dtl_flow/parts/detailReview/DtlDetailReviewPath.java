package com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import flow.path.Path;

@Layout(R.layout.activity_detail_review)
public class DtlDetailReviewPath extends DtlMasterPath {

    private final String  merchantName;
    private final ReviewObject reviewObject;

    public DtlDetailReviewPath(String merchantName, ReviewObject reviewObject) {
        super();
        this.merchantName = merchantName;
        this.reviewObject = reviewObject;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }

    @Override
    public Path getMasterToolbarPath() {
        return MasterToolbarPath.INSTANCE;
    }

    public String getMerchant() {
        return merchantName;
    }

    public ReviewObject getReviewObject() {
        return reviewObject;
    }
}

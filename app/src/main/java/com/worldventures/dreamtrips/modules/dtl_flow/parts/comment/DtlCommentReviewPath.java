package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import flow.path.Path;

@Layout(R.layout.activity_merchant_review)
public class DtlCommentReviewPath extends DtlMasterPath {

    private final Merchant merchant;
    private final boolean isFromListReview;

    public DtlCommentReviewPath(@NonNull Merchant merchant) {
        super();
        this.merchant = merchant;
        this.isFromListReview = false;
    }

    public DtlCommentReviewPath(Merchant merchant, boolean isFromAddReview) {
        this.merchant = merchant;
        this.isFromListReview = isFromAddReview;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }

    @Override
    public Path getMasterToolbarPath() {
        return MasterToolbarPath.INSTANCE;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public boolean isFromAddReview() {
        return isFromListReview;
    }
}

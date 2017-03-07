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
    private final boolean isVerified;

    public DtlCommentReviewPath(@NonNull Merchant merchant) {
        super();
        this.merchant = merchant;
        this.isFromListReview = false;
        this.isVerified = false;
    }

    public DtlCommentReviewPath(Merchant merchant, boolean isFromAddReview, boolean isVerified) {
        this.merchant = merchant;
        this.isFromListReview = isFromAddReview;
        this.isVerified = isVerified;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }

    @Override
    public boolean shouldHideDrawer() {
        return true;
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

    public boolean isVerified() {
        return isVerified;
    }
}

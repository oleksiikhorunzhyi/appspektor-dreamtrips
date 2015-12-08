package com.worldventures.dreamtrips.modules.dtl.helper;

import android.graphics.PointF;
import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;

import java.util.List;

import butterknife.InjectView;

public class DtlMerchantSingleImageDataInflater extends DtlMerchantCommonDataInflater {

    @InjectView(R.id.merchant_details_cover)
    SimpleDraweeView cover;

    final static PointF FOCUS_POINT;

    static {
        FOCUS_POINT = new PointF(0.5f, 0.5f);
    }

    public DtlMerchantSingleImageDataInflater(DtlMerchantHelper helper) {
        super(helper);
    }

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        super.onMerchantApply(merchant);
        setImage(merchant.getImages());
    }

    private void setImage(List<DtlMerchantMedia> mediaList) {
        DtlMerchantMedia media = Queryable.from(mediaList).firstOrDefault();
        if (media == null) {
            return;
        }
        //
        cover.getHierarchy().setActualImageFocusPoint(FOCUS_POINT);
        cover.setImageURI(Uri.parse(media.getImagePath()));
    }
}
